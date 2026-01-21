package com.elvish.plugin.lsp

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

/**
 * Shared utilities for LSP integration tests.
 * Provides common functionality for finding Elvish binary and
 * communicating with the LSP server.
 */
object LspTestUtils {

    /**
     * Finds the Elvish binary in PATH or common locations.
     * @return Path to elvish binary or null if not found
     */
    fun findElvishBinary(): String? {
        // Try common locations
        val candidates = listOf(
            "/opt/homebrew/bin/elvish",  // macOS ARM
            "/usr/local/bin/elvish",      // macOS Intel / Linux
            "/usr/bin/elvish",            // Linux system
            System.getenv("HOME")?.let { "$it/.local/bin/elvish" }  // User local
        ).filterNotNull()

        for (path in candidates) {
            if (File(path).exists() && File(path).canExecute()) {
                return path
            }
        }

        // Try PATH
        val pathDirs = System.getenv("PATH")?.split(File.pathSeparator) ?: return null
        for (dir in pathDirs) {
            val elvish = File(dir, "elvish")
            if (elvish.exists() && elvish.canExecute()) {
                return elvish.absolutePath
            }
        }

        return null
    }

    /**
     * Starts an Elvish LSP server process.
     * @param elvishPath Path to the elvish binary
     * @return Process or null if failed to start
     */
    fun startLspServer(elvishPath: String): Process? {
        return try {
            ProcessBuilder(elvishPath, "-lsp")
                .redirectErrorStream(false)
                .start()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Gracefully shuts down an LSP server process.
     * @param process The LSP server process
     * @param sendRequest Function to send LSP requests
     * @param sendNotification Function to send LSP notifications
     */
    fun shutdownLsp(
        process: Process,
        sendRequest: (String, String) -> String?,
        sendNotification: (String, String) -> Unit
    ) {
        try {
            sendRequest("shutdown", "null")
            sendNotification("exit", "null")
            process.waitFor(2, TimeUnit.SECONDS)
        } finally {
            process.destroyForcibly()
        }
    }

    /**
     * Sends an LSP message to the server.
     * @param output Output stream to the LSP server
     * @param message JSON-RPC message content
     */
    fun sendMessage(output: OutputStream, message: String) {
        val bytes = message.toByteArray(Charsets.UTF_8)
        val header = "Content-Length: ${bytes.size}\r\n\r\n"
        output.write(header.toByteArray(Charsets.UTF_8))
        output.write(bytes)
        output.flush()
    }

    /**
     * Reads an LSP response with the expected ID.
     * @param input Input stream from the LSP server
     * @param expectedId The message ID to wait for
     * @param timeoutMs Timeout in milliseconds
     * @return Response content or null if timeout/error
     */
    fun readResponse(input: InputStream, expectedId: Int, timeoutMs: Long = 5000): String? {
        val timeout = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < timeout) {
            if (input.available() > 0) {
                // Read Content-Length header
                val headerBuilder = StringBuilder()
                var prev = 0
                var curr: Int
                while (input.read().also { curr = it } != -1) {
                    headerBuilder.append(curr.toChar())
                    // Check for \r\n\r\n
                    if (prev == '\r'.code && curr == '\n'.code &&
                        headerBuilder.length >= 4 &&
                        headerBuilder.substring(headerBuilder.length - 4) == "\r\n\r\n") {
                        break
                    }
                    prev = curr
                }

                val header = headerBuilder.toString()
                val lengthMatch = Regex("Content-Length:\\s*(\\d+)").find(header)
                if (lengthMatch != null) {
                    val contentLength = lengthMatch.groupValues[1].toInt()
                    val content = ByteArray(contentLength)
                    var read = 0
                    while (read < contentLength) {
                        val n = input.read(content, read, contentLength - read)
                        if (n == -1) break
                        read += n
                    }
                    val response = String(content, Charsets.UTF_8)
                    // Check if this is the response we're waiting for
                    if (response.contains("\"id\":$expectedId") || response.contains("\"id\": $expectedId")) {
                        return response
                    }
                }
            }
            Thread.sleep(50)
        }
        return null
    }

    /**
     * Escapes a string for JSON.
     * @param text Text to escape
     * @return JSON-safe string with quotes
     */
    fun escapeJson(text: String): String {
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t") + "\""
    }
}
