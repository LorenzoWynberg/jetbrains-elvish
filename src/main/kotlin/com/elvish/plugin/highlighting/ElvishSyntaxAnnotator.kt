package com.elvish.plugin.highlighting

import com.elvish.plugin.ElvishLanguage
import com.elvish.plugin.settings.ElvishSettings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Annotator that highlights syntax errors by running 'elvish -compileonly -c'.
 * Uses caching to avoid running elvish on every keystroke.
 */
class ElvishSyntaxAnnotator : Annotator {

    data class CacheEntry(
        val text: String,
        val diagnostics: List<Diagnostic>,
        val timestamp: Long
    )

    data class Diagnostic(
        val line: Int,
        val column: Int,
        val endColumn: Int,  // -1 means no end column provided
        val message: String
    )

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val file = element.containingFile ?: return
        if (file.language != ElvishLanguage.INSTANCE) return

        // Only run once per file - check if this is the first element
        if (element.prevSibling != null || element.parent !is PsiFile) return

        val document = file.viewProvider.document ?: return
        val text = document.text
        val project = file.project

        // Check cache
        val filePath = file.virtualFile?.path ?: return
        val cached = cache[filePath]
        if (cached != null && cached.text == text && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
            applyDiagnostics(cached.diagnostics, document, holder)
            return
        }

        // Run syntax check
        val elvishPath = ElvishSettings.getInstance(project).elvishPath.ifBlank { "elvish" }
        val diagnostics = checkSyntax(elvishPath, text)

        // Update cache
        cache[filePath] = CacheEntry(text, diagnostics, System.currentTimeMillis())

        LOG.info("Syntax check found ${diagnostics.size} errors for $filePath")
        applyDiagnostics(diagnostics, document, holder)
    }

    private fun applyDiagnostics(
        diagnostics: List<Diagnostic>,
        document: com.intellij.openapi.editor.Document,
        holder: AnnotationHolder
    ) {
        for (diagnostic in diagnostics) {
            try {
                var targetLine = diagnostic.line

                // Handle line out of range (error beyond EOF)
                if (targetLine >= document.lineCount) {
                    targetLine = document.lineCount - 1
                }
                if (targetLine < 0) continue

                var lineStartOffset = document.getLineStartOffset(targetLine)
                var lineEndOffset = document.getLineEndOffset(targetLine)

                // If target line is empty, find the last non-empty line
                while (lineStartOffset == lineEndOffset && targetLine > 0) {
                    targetLine--
                    lineStartOffset = document.getLineStartOffset(targetLine)
                    lineEndOffset = document.getLineEndOffset(targetLine)
                }

                // If still empty (entire file is empty), skip
                if (lineStartOffset == lineEndOffset) continue

                // Calculate highlight range
                val startOffset: Int
                val endOffset: Int
                var displayMessage = diagnostic.message

                if (diagnostic.line == targetLine) {
                    // Error is on this line - use the column
                    startOffset = minOf(lineStartOffset + maxOf(0, diagnostic.column), lineEndOffset)
                    endOffset = if (diagnostic.endColumn > 0) {
                        // Use provided end column
                        minOf(lineStartOffset + diagnostic.endColumn, lineEndOffset)
                    } else {
                        // No end column - highlight to end of line
                        lineEndOffset
                    }
                } else {
                    // Error was on empty line/EOF, we moved up
                    val text = document.text
                    // For "should be" errors (missing brace/bracket), find the opening char
                    if (diagnostic.message.contains("should be '}'")) {
                        // Find the opening brace - search backwards from error position
                        val bracePos = text.lastIndexOf('{', lineEndOffset - 1)
                        if (bracePos >= 0) {
                            startOffset = bracePos
                            endOffset = bracePos + 1
                            // Change message to be clearer
                            displayMessage = "Unclosed '{' - missing '}'"
                        } else {
                            startOffset = lineStartOffset
                            endOffset = lineEndOffset
                        }
                    } else if (diagnostic.message.contains("should be ']'")) {
                        val bracketPos = text.lastIndexOf('[', lineEndOffset - 1)
                        if (bracketPos >= 0) {
                            startOffset = bracketPos
                            endOffset = bracketPos + 1
                            displayMessage = "Unclosed '[' - missing ']'"
                        } else {
                            startOffset = lineStartOffset
                            endOffset = lineEndOffset
                        }
                    } else if (diagnostic.message.contains("should be ')'")) {
                        val parenPos = text.lastIndexOf('(', lineEndOffset - 1)
                        if (parenPos >= 0) {
                            startOffset = parenPos
                            endOffset = parenPos + 1
                            displayMessage = "Unclosed '(' - missing ')'"
                        } else {
                            startOffset = lineStartOffset
                            endOffset = lineEndOffset
                        }
                    } else {
                        // For other errors, highlight the whole line
                        startOffset = lineStartOffset
                        endOffset = lineEndOffset
                    }
                }

                // Ensure valid range (at least 1 character)
                val finalStart = maxOf(0, minOf(startOffset, document.textLength - 1))
                val finalEnd = maxOf(finalStart + 1, minOf(endOffset, document.textLength))
                val range = TextRange(finalStart, finalEnd)

                holder.newAnnotation(HighlightSeverity.ERROR, displayMessage)
                    .range(range)
                    .create()

                LOG.info("Created annotation: $displayMessage at range $range (original line ${diagnostic.line}, target line $targetLine)")
            } catch (e: Exception) {
                LOG.warn("Failed to create annotation: ${e.message}", e)
            }
        }
    }

    private fun checkSyntax(elvishPath: String, code: String): List<Diagnostic> {
        try {
            val process = ProcessBuilder(elvishPath, "-compileonly", "-c", code)
                .redirectErrorStream(true)
                .start()

            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }

            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                return emptyList()
            }

            return parseErrors(output)
        } catch (e: Exception) {
            LOG.warn("Failed to check syntax: ${e.message}")
            return emptyList()
        }
    }

    private fun parseErrors(output: String): List<Diagnostic> {
        val diagnostics = mutableListOf<Diagnostic>()
        val lines = output.lines()
        var currentMessage: String? = null

        for (line in lines) {
            // Error message line: "Parse error: should be '}'"
            val errorMatch = Regex("""(?:Parse |Compilation )?[Ee]rror:\s*(.+)""").find(line)
            if (errorMatch != null) {
                currentMessage = stripAnsi(errorMatch.groupValues[1])
            }

            // Location line: "  code from -c:3:23-25: ..." or "  code from -c:3:23: ..."
            val locMatch = Regex("""code from -c:(\d+):(\d+)(?:-(\d+))?""").find(line)
            if (locMatch != null && currentMessage != null) {
                val lineNum = locMatch.groupValues[1].toIntOrNull() ?: continue
                val col = locMatch.groupValues[2].toIntOrNull() ?: continue
                val endCol = locMatch.groupValues.getOrNull(3)?.toIntOrNull() ?: -1
                diagnostics.add(Diagnostic(lineNum - 1, col - 1, endCol, currentMessage))
                currentMessage = null
            }
        }

        return diagnostics
    }

    private fun stripAnsi(text: String) = text.replace(Regex("""\u001B\[[0-9;]*m"""), "")

    companion object {
        private val LOG = Logger.getInstance(ElvishSyntaxAnnotator::class.java)
        private val cache = ConcurrentHashMap<String, CacheEntry>()
        private const val CACHE_TTL_MS = 2000L
    }
}
