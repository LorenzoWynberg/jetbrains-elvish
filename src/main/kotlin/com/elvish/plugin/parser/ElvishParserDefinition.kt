package com.elvish.plugin.parser

import com.elvish.plugin.ElvishFile
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

/**
 * Parser definition for Elvish language.
 * Ties together the lexer, parser, and PSI file creation.
 */
class ElvishParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = ElvishLexer()

    override fun createParser(project: Project?): PsiParser = ElvishParser()

    override fun getFileNodeType(): IFileElementType = ElvishElementTypes.FILE

    override fun getCommentTokens(): TokenSet = ElvishTokenTypes.COMMENTS

    override fun getStringLiteralElements(): TokenSet = ElvishTokenTypes.STRING_LITERALS

    override fun createElement(node: ASTNode): PsiElement {
        throw UnsupportedOperationException("Not yet implemented: createElement for ${node.elementType}")
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = ElvishFile(viewProvider)
}
