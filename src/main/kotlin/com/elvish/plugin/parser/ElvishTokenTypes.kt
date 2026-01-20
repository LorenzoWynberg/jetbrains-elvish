package com.elvish.plugin.parser

import com.elvish.plugin.ElvishLanguage
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

/**
 * Token types for the Elvish lexer.
 * These define the lexical elements that the lexer will produce when tokenizing Elvish source code.
 */
object ElvishTokenTypes {
    // Comments
    @JvmField
    val COMMENT = ElvishTokenType("COMMENT")

    // String literals
    @JvmField
    val SINGLE_QUOTED_STRING = ElvishTokenType("SINGLE_QUOTED_STRING")

    @JvmField
    val DOUBLE_QUOTED_STRING = ElvishTokenType("DOUBLE_QUOTED_STRING")

    // Numbers
    @JvmField
    val INTEGER = ElvishTokenType("INTEGER")

    @JvmField
    val FLOAT = ElvishTokenType("FLOAT")

    @JvmField
    val HEX_NUMBER = ElvishTokenType("HEX_NUMBER")

    @JvmField
    val OCTAL_NUMBER = ElvishTokenType("OCTAL_NUMBER")

    @JvmField
    val BINARY_NUMBER = ElvishTokenType("BINARY_NUMBER")

    // Keywords - Control flow
    @JvmField
    val IF = ElvishTokenType("IF")

    @JvmField
    val ELIF = ElvishTokenType("ELIF")

    @JvmField
    val ELSE = ElvishTokenType("ELSE")

    @JvmField
    val WHILE = ElvishTokenType("WHILE")

    @JvmField
    val FOR = ElvishTokenType("FOR")

    @JvmField
    val TRY = ElvishTokenType("TRY")

    @JvmField
    val CATCH = ElvishTokenType("CATCH")

    @JvmField
    val FINALLY = ElvishTokenType("FINALLY")

    @JvmField
    val BREAK = ElvishTokenType("BREAK")

    @JvmField
    val CONTINUE = ElvishTokenType("CONTINUE")

    @JvmField
    val RETURN = ElvishTokenType("RETURN")

    // Keywords - Other
    @JvmField
    val FN = ElvishTokenType("FN")

    @JvmField
    val VAR = ElvishTokenType("VAR")

    @JvmField
    val SET = ElvishTokenType("SET")

    @JvmField
    val TMP = ElvishTokenType("TMP")

    @JvmField
    val DEL = ElvishTokenType("DEL")

    @JvmField
    val USE = ElvishTokenType("USE")

    @JvmField
    val PRAGMA = ElvishTokenType("PRAGMA")

    @JvmField
    val AND = ElvishTokenType("AND")

    @JvmField
    val OR = ElvishTokenType("OR")

    @JvmField
    val COALESCE = ElvishTokenType("COALESCE")

    // Identifiers
    @JvmField
    val IDENTIFIER = ElvishTokenType("IDENTIFIER")

    // Variables
    @JvmField
    val VARIABLE = ElvishTokenType("VARIABLE")

    // Constants
    @JvmField
    val TRUE = ElvishTokenType("TRUE")

    @JvmField
    val FALSE = ElvishTokenType("FALSE")

    @JvmField
    val NIL = ElvishTokenType("NIL")

    // Operators
    @JvmField
    val PIPE = ElvishTokenType("PIPE")

    @JvmField
    val EQ = ElvishTokenType("EQ")

    @JvmField
    val NE = ElvishTokenType("NE")

    @JvmField
    val LT = ElvishTokenType("LT")

    @JvmField
    val GT = ElvishTokenType("GT")

    @JvmField
    val LE = ElvishTokenType("LE")

    @JvmField
    val GE = ElvishTokenType("GE")

    @JvmField
    val PLUS = ElvishTokenType("PLUS")

    @JvmField
    val MINUS = ElvishTokenType("MINUS")

    @JvmField
    val STAR = ElvishTokenType("STAR")

    @JvmField
    val SLASH = ElvishTokenType("SLASH")

    @JvmField
    val PERCENT = ElvishTokenType("PERCENT")

    @JvmField
    val ASSIGN = ElvishTokenType("ASSIGN")

    @JvmField
    val RANGE = ElvishTokenType("RANGE")

    @JvmField
    val RANGE_INCLUSIVE = ElvishTokenType("RANGE_INCLUSIVE")

    // Punctuation
    @JvmField
    val LPAREN = ElvishTokenType("LPAREN")

    @JvmField
    val RPAREN = ElvishTokenType("RPAREN")

    @JvmField
    val LBRACKET = ElvishTokenType("LBRACKET")

    @JvmField
    val RBRACKET = ElvishTokenType("RBRACKET")

    @JvmField
    val LBRACE = ElvishTokenType("LBRACE")

    @JvmField
    val RBRACE = ElvishTokenType("RBRACE")

    @JvmField
    val SEMICOLON = ElvishTokenType("SEMICOLON")

    @JvmField
    val AMPERSAND = ElvishTokenType("AMPERSAND")

    @JvmField
    val COLON = ElvishTokenType("COLON")

    @JvmField
    val COMMA = ElvishTokenType("COMMA")

    // Whitespace and special
    @JvmField
    val WHITE_SPACE = ElvishTokenType("WHITE_SPACE")

    @JvmField
    val NEWLINE = ElvishTokenType("NEWLINE")

    @JvmField
    val BAD_CHARACTER = ElvishTokenType("BAD_CHARACTER")

    // Token sets
    @JvmField
    val COMMENTS = TokenSet.create(COMMENT)

    @JvmField
    val STRING_LITERALS = TokenSet.create(SINGLE_QUOTED_STRING, DOUBLE_QUOTED_STRING)

    @JvmField
    val NUMBERS = TokenSet.create(INTEGER, FLOAT, HEX_NUMBER, OCTAL_NUMBER, BINARY_NUMBER)

    @JvmField
    val KEYWORDS = TokenSet.create(
        IF, ELIF, ELSE, WHILE, FOR, TRY, CATCH, FINALLY, BREAK, CONTINUE, RETURN,
        FN, VAR, SET, TMP, DEL, USE, PRAGMA, AND, OR, COALESCE
    )

    @JvmField
    val OPERATORS = TokenSet.create(
        PIPE, EQ, NE, LT, GT, LE, GE, PLUS, MINUS, STAR, SLASH, PERCENT,
        ASSIGN, RANGE, RANGE_INCLUSIVE
    )

    @JvmField
    val CONSTANTS = TokenSet.create(TRUE, FALSE, NIL)

    @JvmField
    val WHITESPACES = TokenSet.create(WHITE_SPACE, NEWLINE)
}

/**
 * Custom IElementType for Elvish tokens.
 * Associates all tokens with the Elvish language.
 */
class ElvishTokenType(debugName: String) : IElementType(debugName, ElvishLanguage.INSTANCE) {
    override fun toString(): String = "ElvishTokenType.${super.toString()}"
}
