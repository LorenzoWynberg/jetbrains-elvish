package com.elvish.plugin.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType

/**
 * Parser for Elvish shell language.
 * Creates a basic AST structure from the token stream produced by ElvishLexer.
 *
 * This is a minimal parser that handles high-level constructs:
 * - Statements (commands, assignments, control flow)
 * - Expressions (pipelines, binary operations)
 * - Declarations (functions, variables)
 *
 * Full language intelligence is provided by the LSP server.
 */
class ElvishParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        parseFile(builder)
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseFile(builder: PsiBuilder) {
        while (!builder.eof()) {
            if (!parseStatement(builder)) {
                // Error recovery: skip unrecognized token and continue
                advanceWithError(builder, "Unexpected token")
            }
        }
    }

    private fun parseStatement(builder: PsiBuilder): Boolean {
        skipWhitespaceAndNewlines(builder)
        if (builder.eof()) return false

        // Don't try to parse RBRACE as a statement - it's a block terminator
        if (builder.tokenType == ElvishTokenTypes.RBRACE) {
            return false
        }

        val marker = builder.mark()
        val parsed = when (builder.tokenType) {
            ElvishTokenTypes.FN -> parseFunctionDefinition(builder, marker)
            ElvishTokenTypes.VAR, ElvishTokenTypes.SET, ElvishTokenTypes.TMP, ElvishTokenTypes.DEL ->
                parseVariableStatement(builder, marker)
            ElvishTokenTypes.IF -> parseIfStatement(builder, marker)
            ElvishTokenTypes.WHILE -> parseWhileStatement(builder, marker)
            ElvishTokenTypes.FOR -> parseForStatement(builder, marker)
            ElvishTokenTypes.TRY -> parseTryStatement(builder, marker)
            ElvishTokenTypes.USE -> parseUseDirective(builder, marker)
            ElvishTokenTypes.PRAGMA -> parsePragmaDirective(builder, marker)
            ElvishTokenTypes.BREAK, ElvishTokenTypes.CONTINUE, ElvishTokenTypes.RETURN -> {
                builder.advanceLexer()
                parseExpressionList(builder)
                marker.done(ElvishElementTypes.STATEMENT)
                true
            }
            ElvishTokenTypes.COMMENT -> {
                builder.advanceLexer()
                marker.drop()
                true
            }
            ElvishTokenTypes.NEWLINE, ElvishTokenTypes.SEMICOLON -> {
                builder.advanceLexer()
                marker.drop()
                true
            }
            else -> parseCommandOrExpression(builder, marker)
        }

        return parsed
    }

    private fun parseFunctionDefinition(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'fn'
        skipWhitespace(builder)

        // Optional function name
        if (builder.tokenType == ElvishTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
            skipWhitespace(builder)
        }

        // Function body block
        if (!parseBlock(builder)) {
            marker.error("Expected function body")
            return true
        }

        marker.done(ElvishElementTypes.FUNCTION_DEFINITION)
        return true
    }

    private fun parseVariableStatement(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        val keyword = builder.tokenType
        builder.advanceLexer() // consume var/set/tmp/del
        skipWhitespace(builder)

        // Variable name(s)
        parseVariableList(builder)
        skipWhitespace(builder)

        // Optional assignment
        if (builder.tokenType == ElvishTokenTypes.ASSIGN) {
            builder.advanceLexer()
            skipWhitespace(builder)
            parseExpressionList(builder)
        }

        val elementType = when (keyword) {
            ElvishTokenTypes.VAR, ElvishTokenTypes.TMP -> ElvishElementTypes.VARIABLE_DECLARATION
            else -> ElvishElementTypes.ASSIGNMENT
        }
        marker.done(elementType)
        return true
    }

    private fun parseVariableList(builder: PsiBuilder) {
        // Only parse identifiers as loop variables (not $variables)
        // In Elvish, `for item $list { ... }` - 'item' is variable name, '$list' is iterable
        while (!builder.eof()) {
            if (builder.tokenType == ElvishTokenTypes.IDENTIFIER) {
                builder.advanceLexer()
                skipWhitespace(builder)
            } else {
                break
            }
        }
    }

    private fun parseIfStatement(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'if'
        skipWhitespace(builder)

        // Condition
        parseCondition(builder)
        skipWhitespace(builder)

        // Then block
        if (!parseBlock(builder)) {
            marker.error("Expected block after if condition")
            return true
        }

        // Optional elif/else clauses
        skipWhitespaceAndNewlines(builder)
        while (builder.tokenType == ElvishTokenTypes.ELIF) {
            val elifMarker = builder.mark()
            builder.advanceLexer() // consume 'elif'
            skipWhitespace(builder)
            parseCondition(builder)
            skipWhitespace(builder)
            if (!parseBlock(builder)) {
                elifMarker.error("Expected block after elif condition")
            } else {
                elifMarker.done(ElvishElementTypes.ELIF_CLAUSE)
            }
            skipWhitespaceAndNewlines(builder)
        }

        if (builder.tokenType == ElvishTokenTypes.ELSE) {
            val elseMarker = builder.mark()
            builder.advanceLexer() // consume 'else'
            skipWhitespace(builder)
            if (!parseBlock(builder)) {
                elseMarker.error("Expected block after else")
            } else {
                elseMarker.done(ElvishElementTypes.ELSE_CLAUSE)
            }
        }

        marker.done(ElvishElementTypes.IF_STATEMENT)
        return true
    }

    private fun parseWhileStatement(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'while'
        skipWhitespace(builder)

        parseCondition(builder)
        skipWhitespace(builder)

        if (!parseBlock(builder)) {
            marker.error("Expected block after while condition")
            return true
        }

        marker.done(ElvishElementTypes.WHILE_STATEMENT)
        return true
    }

    private fun parseForStatement(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'for'
        skipWhitespace(builder)

        // Loop variable(s)
        parseVariableList(builder)
        skipWhitespace(builder)

        // Iterable expression - stop at LBRACE
        parseExpressionUntilBlock(builder)
        skipWhitespace(builder)

        if (!parseBlock(builder)) {
            marker.error("Expected block after for expression")
            return true
        }

        marker.done(ElvishElementTypes.FOR_STATEMENT)
        return true
    }

    private fun parseTryStatement(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'try'
        skipWhitespace(builder)

        if (!parseBlock(builder)) {
            marker.error("Expected block after try")
            return true
        }

        skipWhitespaceAndNewlines(builder)

        // Optional catch clause
        if (builder.tokenType == ElvishTokenTypes.CATCH) {
            val catchMarker = builder.mark()
            builder.advanceLexer()
            skipWhitespace(builder)

            // Optional variable name
            if (builder.tokenType == ElvishTokenTypes.IDENTIFIER) {
                builder.advanceLexer()
                skipWhitespace(builder)
            }

            if (!parseBlock(builder)) {
                catchMarker.error("Expected block after catch")
            } else {
                catchMarker.done(ElvishElementTypes.CATCH_CLAUSE)
            }
            skipWhitespaceAndNewlines(builder)
        }

        // Optional finally clause
        if (builder.tokenType == ElvishTokenTypes.FINALLY) {
            val finallyMarker = builder.mark()
            builder.advanceLexer()
            skipWhitespace(builder)
            if (!parseBlock(builder)) {
                finallyMarker.error("Expected block after finally")
            } else {
                finallyMarker.done(ElvishElementTypes.FINALLY_CLAUSE)
            }
        }

        marker.done(ElvishElementTypes.TRY_STATEMENT)
        return true
    }

    private fun parseUseDirective(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'use'
        skipWhitespace(builder)

        // Module name
        if (builder.tokenType == ElvishTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
        }

        marker.done(ElvishElementTypes.USE_DIRECTIVE)
        return true
    }

    private fun parsePragmaDirective(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        builder.advanceLexer() // consume 'pragma'
        skipWhitespace(builder)

        // Pragma options
        while (!builder.eof() && !isStatementTerminator(builder.tokenType)) {
            builder.advanceLexer()
        }

        marker.done(ElvishElementTypes.PRAGMA_DIRECTIVE)
        return true
    }

    private fun parseCommandOrExpression(builder: PsiBuilder, marker: PsiBuilder.Marker): Boolean {
        // Try to parse as a command or pipeline
        if (!parseExpression(builder)) {
            marker.drop()
            return false
        }

        // Check for pipeline
        skipWhitespace(builder)
        if (builder.tokenType == ElvishTokenTypes.PIPE) {
            val pipelineMarker = marker.precede()
            marker.done(ElvishElementTypes.COMMAND)

            while (builder.tokenType == ElvishTokenTypes.PIPE) {
                builder.advanceLexer()
                skipWhitespace(builder)
                val cmdMarker = builder.mark()
                if (!parseExpression(builder)) {
                    cmdMarker.error("Expected command after pipe")
                    break
                }
                cmdMarker.done(ElvishElementTypes.COMMAND)
                skipWhitespace(builder)
            }
            pipelineMarker.done(ElvishElementTypes.PIPELINE)
        } else {
            marker.done(ElvishElementTypes.STATEMENT)
        }

        return true
    }

    private fun parseBlock(builder: PsiBuilder): Boolean {
        if (builder.tokenType != ElvishTokenTypes.LBRACE) {
            return false
        }

        val marker = builder.mark()
        builder.advanceLexer() // consume '{'

        // Optional parameter list |param1 param2|
        skipWhitespace(builder)
        if (builder.tokenType == ElvishTokenTypes.PIPE) {
            parseParameterList(builder)
        }

        // Block contents
        while (true) {
            skipWhitespaceAndNewlines(builder)
            if (builder.eof() || builder.tokenType == ElvishTokenTypes.RBRACE) {
                break
            }
            if (!parseStatement(builder)) {
                advanceWithError(builder, "Unexpected token in block")
            }
        }

        if (builder.tokenType == ElvishTokenTypes.RBRACE) {
            builder.advanceLexer()
        } else {
            marker.error("Expected '}'")
            return true
        }

        marker.done(ElvishElementTypes.BLOCK)
        return true
    }

    private fun parseParameterList(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume first '|'

        skipWhitespace(builder)
        while (!builder.eof() && builder.tokenType != ElvishTokenTypes.PIPE) {
            if (builder.tokenType == ElvishTokenTypes.IDENTIFIER ||
                builder.tokenType == ElvishTokenTypes.AMPERSAND) {
                builder.advanceLexer()
            }
            skipWhitespace(builder)
        }

        if (builder.tokenType == ElvishTokenTypes.PIPE) {
            builder.advanceLexer() // consume closing '|'
        }

        marker.done(ElvishElementTypes.PARAMETER_LIST)
        skipWhitespace(builder)
    }

    private fun parseCondition(builder: PsiBuilder): Boolean {
        // Parse condition expression - stops at LBRACE (block start)
        return parseExpressionUntilBlock(builder)
    }

    private fun parseExpressionUntilBlock(builder: PsiBuilder): Boolean {
        skipWhitespace(builder)
        if (builder.eof() || isStatementTerminator(builder.tokenType)) {
            return false
        }

        val marker = builder.mark()
        if (!parsePrimaryExpressionNotBlock(builder)) {
            marker.drop()
            return false
        }

        // Parse binary operators but stop at LBRACE
        skipWhitespace(builder)
        while (!builder.eof() && !isStatementTerminator(builder.tokenType) &&
               builder.tokenType != ElvishTokenTypes.RBRACE &&
               builder.tokenType != ElvishTokenTypes.LBRACE &&
               builder.tokenType != ElvishTokenTypes.PIPE) {

            if (isBinaryOperator(builder.tokenType)) {
                val opMarker = marker.precede()
                marker.done(ElvishElementTypes.EXPRESSION)
                builder.advanceLexer()
                skipWhitespace(builder)
                if (!parsePrimaryExpressionNotBlock(builder)) {
                    opMarker.error("Expected expression after operator")
                    return true
                }
                opMarker.done(ElvishElementTypes.BINARY_EXPRESSION)
                skipWhitespace(builder)
            } else if (canStartExpressionNotBlock(builder.tokenType)) {
                parsePrimaryExpressionNotBlock(builder)
                skipWhitespace(builder)
            } else {
                break
            }
        }

        marker.done(ElvishElementTypes.EXPRESSION)
        return true
    }

    private fun parseExpression(builder: PsiBuilder): Boolean {
        skipWhitespace(builder)
        if (builder.eof() || isStatementTerminator(builder.tokenType)) {
            return false
        }

        val marker = builder.mark()
        if (!parsePrimaryExpression(builder)) {
            marker.drop()
            return false
        }

        // Parse binary operators and more arguments
        skipWhitespace(builder)
        while (!builder.eof() && !isStatementTerminator(builder.tokenType) &&
               builder.tokenType != ElvishTokenTypes.RBRACE &&
               builder.tokenType != ElvishTokenTypes.PIPE) {

            if (isBinaryOperator(builder.tokenType)) {
                // Binary expression
                val opMarker = marker.precede()
                marker.done(ElvishElementTypes.EXPRESSION)
                builder.advanceLexer() // consume operator
                skipWhitespace(builder)
                if (!parsePrimaryExpression(builder)) {
                    opMarker.error("Expected expression after operator")
                    return true
                }
                opMarker.done(ElvishElementTypes.BINARY_EXPRESSION)
                skipWhitespace(builder)
            } else if (canStartExpression(builder.tokenType)) {
                // Additional argument in command
                parsePrimaryExpression(builder)
                skipWhitespace(builder)
            } else {
                break
            }
        }

        marker.done(ElvishElementTypes.EXPRESSION)
        return true
    }

    private fun parsePrimaryExpression(builder: PsiBuilder): Boolean {
        return when (builder.tokenType) {
            ElvishTokenTypes.VARIABLE -> parseVariableReference(builder)
            ElvishTokenTypes.TRUE, ElvishTokenTypes.FALSE, ElvishTokenTypes.NIL -> {
                builder.advanceLexer()
                true
            }
            ElvishTokenTypes.INTEGER, ElvishTokenTypes.FLOAT,
            ElvishTokenTypes.HEX_NUMBER, ElvishTokenTypes.OCTAL_NUMBER,
            ElvishTokenTypes.BINARY_NUMBER -> parseNumberLiteral(builder)
            ElvishTokenTypes.SINGLE_QUOTED_STRING, ElvishTokenTypes.DOUBLE_QUOTED_STRING ->
                parseStringLiteral(builder)
            ElvishTokenTypes.LBRACKET -> parseListLiteral(builder)
            ElvishTokenTypes.LBRACE -> {
                // Could be map literal or block - treat as block for now
                parseBlock(builder)
            }
            ElvishTokenTypes.LPAREN -> parseGroupedExpression(builder)
            ElvishTokenTypes.IDENTIFIER -> {
                builder.advanceLexer()
                true
            }
            else -> false
        }
    }

    private fun parseVariableReference(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume variable

        // Check for indexing [...]
        if (builder.tokenType == ElvishTokenTypes.LBRACKET) {
            builder.advanceLexer()
            parseExpression(builder)
            if (builder.tokenType == ElvishTokenTypes.RBRACKET) {
                builder.advanceLexer()
            }
            marker.done(ElvishElementTypes.INDEXING)
        } else {
            marker.done(ElvishElementTypes.VARIABLE_REFERENCE)
        }
        return true
    }

    private fun parseNumberLiteral(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        marker.done(ElvishElementTypes.NUMBER_LITERAL)
        return true
    }

    private fun parseStringLiteral(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        marker.done(ElvishElementTypes.STRING_LITERAL)
        return true
    }

    private fun parseListLiteral(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume '['

        skipWhitespace(builder)
        while (!builder.eof() && builder.tokenType != ElvishTokenTypes.RBRACKET) {
            if (!parseExpression(builder)) {
                advanceWithError(builder, "Expected expression in list")
            }
            skipWhitespace(builder)
        }

        if (builder.tokenType == ElvishTokenTypes.RBRACKET) {
            builder.advanceLexer()
        }

        marker.done(ElvishElementTypes.LIST_LITERAL)
        return true
    }

    private fun parseGroupedExpression(builder: PsiBuilder): Boolean {
        builder.advanceLexer() // consume '('
        skipWhitespace(builder)

        parseExpression(builder)

        skipWhitespace(builder)
        if (builder.tokenType == ElvishTokenTypes.RPAREN) {
            builder.advanceLexer()
        }

        return true
    }

    private fun parseExpressionList(builder: PsiBuilder) {
        skipWhitespace(builder)
        while (!builder.eof() && !isStatementTerminator(builder.tokenType)) {
            if (!parseExpression(builder)) {
                break
            }
            skipWhitespace(builder)
        }
    }

    private fun skipWhitespace(builder: PsiBuilder) {
        while (builder.tokenType == ElvishTokenTypes.WHITE_SPACE) {
            builder.advanceLexer()
        }
    }

    private fun skipWhitespaceAndNewlines(builder: PsiBuilder) {
        while (builder.tokenType == ElvishTokenTypes.WHITE_SPACE ||
               builder.tokenType == ElvishTokenTypes.NEWLINE) {
            builder.advanceLexer()
        }
    }

    private fun isStatementTerminator(tokenType: IElementType?): Boolean {
        return tokenType == null ||
               tokenType == ElvishTokenTypes.NEWLINE ||
               tokenType == ElvishTokenTypes.SEMICOLON ||
               tokenType == ElvishTokenTypes.COMMENT
    }

    private fun isBinaryOperator(tokenType: IElementType?): Boolean {
        return tokenType == ElvishTokenTypes.EQ ||
               tokenType == ElvishTokenTypes.NE ||
               tokenType == ElvishTokenTypes.LT ||
               tokenType == ElvishTokenTypes.GT ||
               tokenType == ElvishTokenTypes.LE ||
               tokenType == ElvishTokenTypes.GE ||
               tokenType == ElvishTokenTypes.PLUS ||
               tokenType == ElvishTokenTypes.MINUS ||
               tokenType == ElvishTokenTypes.STAR ||
               tokenType == ElvishTokenTypes.SLASH ||
               tokenType == ElvishTokenTypes.PERCENT ||
               tokenType == ElvishTokenTypes.AND ||
               tokenType == ElvishTokenTypes.OR ||
               tokenType == ElvishTokenTypes.COALESCE ||
               tokenType == ElvishTokenTypes.RANGE ||
               tokenType == ElvishTokenTypes.RANGE_INCLUSIVE
    }

    private fun canStartExpression(tokenType: IElementType?): Boolean {
        return tokenType == ElvishTokenTypes.IDENTIFIER ||
               tokenType == ElvishTokenTypes.VARIABLE ||
               tokenType == ElvishTokenTypes.INTEGER ||
               tokenType == ElvishTokenTypes.FLOAT ||
               tokenType == ElvishTokenTypes.HEX_NUMBER ||
               tokenType == ElvishTokenTypes.OCTAL_NUMBER ||
               tokenType == ElvishTokenTypes.BINARY_NUMBER ||
               tokenType == ElvishTokenTypes.SINGLE_QUOTED_STRING ||
               tokenType == ElvishTokenTypes.DOUBLE_QUOTED_STRING ||
               tokenType == ElvishTokenTypes.TRUE ||
               tokenType == ElvishTokenTypes.FALSE ||
               tokenType == ElvishTokenTypes.NIL ||
               tokenType == ElvishTokenTypes.LBRACKET ||
               tokenType == ElvishTokenTypes.LBRACE ||
               tokenType == ElvishTokenTypes.LPAREN
    }

    private fun canStartExpressionNotBlock(tokenType: IElementType?): Boolean {
        return tokenType == ElvishTokenTypes.IDENTIFIER ||
               tokenType == ElvishTokenTypes.VARIABLE ||
               tokenType == ElvishTokenTypes.INTEGER ||
               tokenType == ElvishTokenTypes.FLOAT ||
               tokenType == ElvishTokenTypes.HEX_NUMBER ||
               tokenType == ElvishTokenTypes.OCTAL_NUMBER ||
               tokenType == ElvishTokenTypes.BINARY_NUMBER ||
               tokenType == ElvishTokenTypes.SINGLE_QUOTED_STRING ||
               tokenType == ElvishTokenTypes.DOUBLE_QUOTED_STRING ||
               tokenType == ElvishTokenTypes.TRUE ||
               tokenType == ElvishTokenTypes.FALSE ||
               tokenType == ElvishTokenTypes.NIL ||
               tokenType == ElvishTokenTypes.LBRACKET ||
               tokenType == ElvishTokenTypes.LPAREN
        // Note: LBRACE excluded to stop at block start
    }

    private fun parsePrimaryExpressionNotBlock(builder: PsiBuilder): Boolean {
        return when (builder.tokenType) {
            ElvishTokenTypes.VARIABLE -> parseVariableReference(builder)
            ElvishTokenTypes.TRUE, ElvishTokenTypes.FALSE, ElvishTokenTypes.NIL -> {
                builder.advanceLexer()
                true
            }
            ElvishTokenTypes.INTEGER, ElvishTokenTypes.FLOAT,
            ElvishTokenTypes.HEX_NUMBER, ElvishTokenTypes.OCTAL_NUMBER,
            ElvishTokenTypes.BINARY_NUMBER -> parseNumberLiteral(builder)
            ElvishTokenTypes.SINGLE_QUOTED_STRING, ElvishTokenTypes.DOUBLE_QUOTED_STRING ->
                parseStringLiteral(builder)
            ElvishTokenTypes.LBRACKET -> parseListLiteral(builder)
            ElvishTokenTypes.LPAREN -> parseGroupedExpression(builder)
            ElvishTokenTypes.IDENTIFIER -> {
                builder.advanceLexer()
                true
            }
            // Note: LBRACE not handled here - blocks stop condition parsing
            else -> false
        }
    }

    private fun advanceWithError(builder: PsiBuilder, message: String) {
        val marker = builder.mark()
        builder.advanceLexer()
        marker.error(message)
    }
}
