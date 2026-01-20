package com.elvish.plugin.parser

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * Lexer for Elvish shell language.
 * Tokenizes Elvish source code into IElementType tokens.
 */
class ElvishLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentToken: IElementType? = null
    private var state: Int = 0

    companion object {
        private val KEYWORDS = mapOf(
            "if" to ElvishTokenTypes.IF,
            "elif" to ElvishTokenTypes.ELIF,
            "else" to ElvishTokenTypes.ELSE,
            "while" to ElvishTokenTypes.WHILE,
            "for" to ElvishTokenTypes.FOR,
            "try" to ElvishTokenTypes.TRY,
            "catch" to ElvishTokenTypes.CATCH,
            "finally" to ElvishTokenTypes.FINALLY,
            "break" to ElvishTokenTypes.BREAK,
            "continue" to ElvishTokenTypes.CONTINUE,
            "return" to ElvishTokenTypes.RETURN,
            "fn" to ElvishTokenTypes.FN,
            "var" to ElvishTokenTypes.VAR,
            "set" to ElvishTokenTypes.SET,
            "tmp" to ElvishTokenTypes.TMP,
            "del" to ElvishTokenTypes.DEL,
            "use" to ElvishTokenTypes.USE,
            "pragma" to ElvishTokenTypes.PRAGMA,
            "and" to ElvishTokenTypes.AND,
            "or" to ElvishTokenTypes.OR,
            "coalesce" to ElvishTokenTypes.COALESCE
        )
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.state = initialState
        advance()
    }

    override fun getState(): Int = state

    override fun getTokenType(): IElementType? = currentToken

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd

        if (tokenStart >= bufferEnd) {
            currentToken = null
            return
        }

        val c = buffer[tokenStart]

        currentToken = when {
            c == '#' -> lexComment()
            c == '\'' -> lexSingleQuotedString()
            c == '"' -> lexDoubleQuotedString()
            c == '$' -> lexVariable()
            c.isDigit() -> lexNumber()
            c == '-' && tokenStart + 1 < bufferEnd && buffer[tokenStart + 1].isDigit() -> lexNumber()
            c.isLetter() || c == '_' -> lexIdentifierOrKeyword()
            c.isWhitespace() -> lexWhitespace(c)
            else -> lexOperatorOrPunctuation(c)
        }
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    private fun lexComment(): IElementType {
        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\n') {
            tokenEnd++
        }
        return ElvishTokenTypes.COMMENT
    }

    private fun lexSingleQuotedString(): IElementType {
        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd) {
            val c = buffer[tokenEnd]
            if (c == '\'') {
                tokenEnd++
                // Check for escaped quote ('')
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '\'') {
                    tokenEnd++
                    continue
                }
                break
            }
            tokenEnd++
        }
        return ElvishTokenTypes.SINGLE_QUOTED_STRING
    }

    private fun lexDoubleQuotedString(): IElementType {
        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd) {
            val c = buffer[tokenEnd]
            if (c == '\\' && tokenEnd + 1 < bufferEnd) {
                // Skip escaped character
                tokenEnd += 2
                continue
            }
            if (c == '"') {
                tokenEnd++
                break
            }
            tokenEnd++
        }
        return ElvishTokenTypes.DOUBLE_QUOTED_STRING
    }

    private fun lexVariable(): IElementType {
        tokenEnd = tokenStart + 1

        // Handle $@ for explode
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '@') {
            tokenEnd++
        }

        // Handle variable name (alphanumeric, underscore, colon for namespaced)
        while (tokenEnd < bufferEnd) {
            val c = buffer[tokenEnd]
            if (c.isLetterOrDigit() || c == '_' || c == ':' || c == '-') {
                tokenEnd++
            } else {
                break
            }
        }

        // Check for special constant variables
        val varName = buffer.subSequence(tokenStart, tokenEnd).toString()
        return when (varName) {
            "\$true" -> ElvishTokenTypes.TRUE
            "\$false" -> ElvishTokenTypes.FALSE
            "\$nil" -> ElvishTokenTypes.NIL
            else -> ElvishTokenTypes.VARIABLE
        }
    }

    private fun lexNumber(): IElementType {
        tokenEnd = tokenStart

        // Handle optional negative sign
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '-') {
            tokenEnd++
        }

        // Check for hex, octal, or binary prefix
        if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '0') {
            when (buffer[tokenEnd + 1]) {
                'x', 'X' -> return lexHexNumber()
                'o', 'O' -> return lexOctalNumber()
                'b', 'B' -> return lexBinaryNumber()
            }
        }

        // Check for special constants
        if (matchesKeyword("Inf") || matchesKeyword("-Inf")) {
            tokenEnd = tokenStart + (if (buffer[tokenStart] == '-') 4 else 3)
            return ElvishTokenTypes.FLOAT
        }
        if (matchesKeyword("NaN")) {
            tokenEnd = tokenStart + 3
            return ElvishTokenTypes.FLOAT
        }

        // Lex integer part
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
            tokenEnd++
        }

        // Check for float or rational
        if (tokenEnd < bufferEnd) {
            when (buffer[tokenEnd]) {
                '.' -> {
                    // Float
                    tokenEnd++
                    while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
                        tokenEnd++
                    }
                    // Check for scientific notation
                    if (tokenEnd < bufferEnd && (buffer[tokenEnd] == 'e' || buffer[tokenEnd] == 'E')) {
                        tokenEnd++
                        if (tokenEnd < bufferEnd && (buffer[tokenEnd] == '+' || buffer[tokenEnd] == '-')) {
                            tokenEnd++
                        }
                        while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
                            tokenEnd++
                        }
                    }
                    return ElvishTokenTypes.FLOAT
                }
                '/' -> {
                    // Could be rational or division - check if followed by digits only
                    val startPos = tokenEnd
                    tokenEnd++
                    if (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
                        while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
                            tokenEnd++
                        }
                        // Rational only if we consumed some digits
                        return ElvishTokenTypes.FLOAT
                    }
                    // Not a rational, reset
                    tokenEnd = startPos
                }
                'e', 'E' -> {
                    // Scientific notation
                    tokenEnd++
                    if (tokenEnd < bufferEnd && (buffer[tokenEnd] == '+' || buffer[tokenEnd] == '-')) {
                        tokenEnd++
                    }
                    while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
                        tokenEnd++
                    }
                    return ElvishTokenTypes.FLOAT
                }
            }
        }

        return ElvishTokenTypes.INTEGER
    }

    private fun matchesKeyword(keyword: String): Boolean {
        if (tokenStart + keyword.length > bufferEnd) return false
        for (i in keyword.indices) {
            if (buffer[tokenStart + i] != keyword[i]) return false
        }
        // Ensure it's not part of a longer identifier
        if (tokenStart + keyword.length < bufferEnd) {
            val next = buffer[tokenStart + keyword.length]
            if (next.isLetterOrDigit() || next == '_') return false
        }
        return true
    }

    private fun lexHexNumber(): IElementType {
        tokenEnd += 2 // Skip 0x
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isHexDigit()) {
            tokenEnd++
        }
        return ElvishTokenTypes.HEX_NUMBER
    }

    private fun lexOctalNumber(): IElementType {
        tokenEnd += 2 // Skip 0o
        while (tokenEnd < bufferEnd && buffer[tokenEnd] in '0'..'7') {
            tokenEnd++
        }
        return ElvishTokenTypes.OCTAL_NUMBER
    }

    private fun lexBinaryNumber(): IElementType {
        tokenEnd += 2 // Skip 0b
        while (tokenEnd < bufferEnd && (buffer[tokenEnd] == '0' || buffer[tokenEnd] == '1')) {
            tokenEnd++
        }
        return ElvishTokenTypes.BINARY_NUMBER
    }

    private fun Char.isHexDigit(): Boolean =
        this.isDigit() || this in 'a'..'f' || this in 'A'..'F'

    private fun lexIdentifierOrKeyword(): IElementType {
        tokenEnd = tokenStart
        while (tokenEnd < bufferEnd) {
            val c = buffer[tokenEnd]
            if (c.isLetterOrDigit() || c == '_' || c == '-') {
                tokenEnd++
            } else {
                break
            }
        }

        val text = buffer.subSequence(tokenStart, tokenEnd).toString()
        return KEYWORDS[text] ?: ElvishTokenTypes.IDENTIFIER
    }

    private fun lexWhitespace(c: Char): IElementType {
        if (c == '\n') {
            tokenEnd = tokenStart + 1
            return ElvishTokenTypes.NEWLINE
        }

        tokenEnd = tokenStart + 1
        while (tokenEnd < bufferEnd) {
            val next = buffer[tokenEnd]
            if (next.isWhitespace() && next != '\n') {
                tokenEnd++
            } else {
                break
            }
        }
        return ElvishTokenTypes.WHITE_SPACE
    }

    private fun lexOperatorOrPunctuation(c: Char): IElementType {
        tokenEnd = tokenStart + 1

        return when (c) {
            '|' -> ElvishTokenTypes.PIPE
            '(' -> ElvishTokenTypes.LPAREN
            ')' -> ElvishTokenTypes.RPAREN
            '[' -> ElvishTokenTypes.LBRACKET
            ']' -> ElvishTokenTypes.RBRACKET
            '{' -> ElvishTokenTypes.LBRACE
            '}' -> ElvishTokenTypes.RBRACE
            ';' -> ElvishTokenTypes.SEMICOLON
            '&' -> ElvishTokenTypes.AMPERSAND
            ':' -> ElvishTokenTypes.COLON
            ',' -> ElvishTokenTypes.COMMA
            '+' -> ElvishTokenTypes.PLUS
            '-' -> ElvishTokenTypes.MINUS
            '*' -> ElvishTokenTypes.STAR
            '/' -> ElvishTokenTypes.SLASH
            '%' -> ElvishTokenTypes.PERCENT
            '=' -> {
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '=') {
                    tokenEnd++
                    ElvishTokenTypes.EQ
                } else {
                    ElvishTokenTypes.ASSIGN
                }
            }
            '!' -> {
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '=') {
                    tokenEnd++
                    ElvishTokenTypes.NE
                } else {
                    ElvishTokenTypes.BAD_CHARACTER
                }
            }
            '<' -> {
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '=') {
                    tokenEnd++
                    ElvishTokenTypes.LE
                } else {
                    ElvishTokenTypes.LT
                }
            }
            '>' -> {
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '=') {
                    tokenEnd++
                    ElvishTokenTypes.GE
                } else if (tokenEnd < bufferEnd && buffer[tokenEnd] == '>') {
                    tokenEnd++
                    ElvishTokenTypes.IDENTIFIER // >> redirect, treat as identifier for now
                } else {
                    ElvishTokenTypes.GT
                }
            }
            '.' -> {
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '.') {
                    tokenEnd++
                    if (tokenEnd < bufferEnd && buffer[tokenEnd] == '=') {
                        tokenEnd++
                        ElvishTokenTypes.RANGE_INCLUSIVE
                    } else {
                        ElvishTokenTypes.RANGE
                    }
                } else {
                    ElvishTokenTypes.BAD_CHARACTER
                }
            }
            else -> ElvishTokenTypes.BAD_CHARACTER
        }
    }
}
