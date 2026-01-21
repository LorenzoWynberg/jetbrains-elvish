package com.elvish.plugin.editor

import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

/**
 * Brace matcher for Elvish files.
 *
 * Provides matching for:
 * - Curly braces { } - used for function bodies and control flow blocks
 * - Square brackets [ ] - used for lists and maps
 * - Parentheses ( ) - used for output capture (command substitution)
 */
class ElvishBraceMatcher : PairedBraceMatcher {

    companion object {
        private val BRACE_PAIRS = arrayOf(
            // Curly braces are structural - used for function bodies and blocks
            BracePair(ElvishTokenTypes.LBRACE, ElvishTokenTypes.RBRACE, true),
            // Square brackets - used for lists and maps
            BracePair(ElvishTokenTypes.LBRACKET, ElvishTokenTypes.RBRACKET, false),
            // Parentheses - used for output capture (command substitution)
            BracePair(ElvishTokenTypes.LPAREN, ElvishTokenTypes.RPAREN, false)
        )
    }

    override fun getPairs(): Array<BracePair> = BRACE_PAIRS

    override fun isPairedBracesAllowedBeforeType(
        lbraceType: IElementType,
        contextType: IElementType?
    ): Boolean {
        // Allow paired braces before whitespace, newlines, and closing brackets
        return contextType == null ||
                contextType == ElvishTokenTypes.WHITE_SPACE ||
                contextType == ElvishTokenTypes.NEWLINE ||
                contextType == ElvishTokenTypes.COMMENT ||
                contextType == ElvishTokenTypes.RBRACE ||
                contextType == ElvishTokenTypes.RBRACKET ||
                contextType == ElvishTokenTypes.RPAREN ||
                contextType == ElvishTokenTypes.SEMICOLON ||
                contextType == ElvishTokenTypes.PIPE
    }

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
        // Return the same offset - we don't need special handling for code construct start
        return openingBraceOffset
    }
}
