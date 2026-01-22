package com.elvish.plugin.editor

import com.elvish.plugin.parser.ElvishLexer
import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.search.IndexPatternBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

/**
 * Index pattern builder for Elvish.
 * Enables TODO/FIXME/XXX/HACK/BUG highlighting in # comments.
 *
 * This class tells IntelliJ how to find and parse TODO patterns
 * within Elvish comment tokens.
 */
class ElvishIndexPatternBuilder : IndexPatternBuilder {

    /**
     * Returns the lexer used for indexing patterns in the file.
     */
    override fun getIndexingLexer(file: PsiFile): Lexer? {
        return ElvishLexer()
    }

    /**
     * Returns the token types that should be scanned for TODO patterns.
     * For Elvish, only COMMENT tokens contain potential TODO items.
     */
    override fun getCommentTokenSet(file: PsiFile): TokenSet? {
        return ElvishTokenTypes.COMMENTS
    }

    /**
     * Returns the number of characters to skip at the start of the comment.
     * For Elvish # comments, we skip 1 character (the # itself).
     *
     * @param tokenType The type of comment token
     * @return Number of characters to skip (1 for # prefix)
     */
    override fun getCommentStartDelta(tokenType: IElementType): Int {
        return if (tokenType == ElvishTokenTypes.COMMENT) 1 else 0
    }

    /**
     * Returns the number of characters to skip at the end of the comment.
     * Elvish line comments don't have a closing delimiter.
     *
     * @param tokenType The type of comment token
     * @return Number of characters to skip (0 for line comments)
     */
    override fun getCommentEndDelta(tokenType: IElementType): Int {
        return 0
    }
}
