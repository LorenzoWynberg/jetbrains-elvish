package com.elvish.plugin.editor

import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer

/**
 * Spell checking strategy for Elvish files.
 *
 * Enables spell checking for:
 * - Comments (# line comments)
 * - String literals (single-quoted 'text' and double-quoted "text")
 *
 * Disables spell checking for:
 * - Barewords/identifiers (often commands, paths, or variable names)
 * - Keywords
 * - Operators
 * - Numbers
 */
class ElvishSpellcheckingStrategy : SpellcheckingStrategy() {

    override fun getTokenizer(element: PsiElement): Tokenizer<*> {
        val elementType = element.node?.elementType

        return when {
            isCommentToken(elementType) -> TEXT_TOKENIZER
            isStringToken(elementType) -> TEXT_TOKENIZER
            else -> EMPTY_TOKENIZER
        }
    }

    /**
     * Check if the element type is a comment token.
     */
    private fun isCommentToken(elementType: IElementType?): Boolean {
        return elementType == ElvishTokenTypes.COMMENT
    }

    /**
     * Check if the element type is a string token.
     */
    private fun isStringToken(elementType: IElementType?): Boolean {
        return elementType == ElvishTokenTypes.SINGLE_QUOTED_STRING ||
               elementType == ElvishTokenTypes.DOUBLE_QUOTED_STRING
    }
}
