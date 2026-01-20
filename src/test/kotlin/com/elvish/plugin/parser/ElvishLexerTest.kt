package com.elvish.plugin.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ElvishLexerTest {
    private fun tokenize(text: String): List<Pair<String, String>> {
        val lexer = ElvishLexer()
        lexer.start(text, 0, text.length, 0)
        val tokens = mutableListOf<Pair<String, String>>()
        while (lexer.tokenType != null) {
            val tokenText = text.substring(lexer.tokenStart, lexer.tokenEnd)
            val tokenType = lexer.tokenType.toString()
            tokens.add(tokenType to tokenText)
            lexer.advance()
        }
        return tokens
    }

    @Test
    fun testComments() {
        val tokens = tokenize("# this is a comment\nfoo")
        assertEquals(3, tokens.size)
        assertEquals("ElvishTokenType.COMMENT" to "# this is a comment", tokens[0])
        assertEquals("ElvishTokenType.NEWLINE" to "\n", tokens[1])
        assertEquals("ElvishTokenType.IDENTIFIER" to "foo", tokens[2])
    }

    @Test
    fun testSingleQuotedString() {
        val tokens = tokenize("'hello world'")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.SINGLE_QUOTED_STRING" to "'hello world'", tokens[0])
    }

    @Test
    fun testSingleQuotedStringWithEscape() {
        val tokens = tokenize("'it''s escaped'")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.SINGLE_QUOTED_STRING" to "'it''s escaped'", tokens[0])
    }

    @Test
    fun testDoubleQuotedString() {
        val tokens = tokenize("\"hello world\"")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.DOUBLE_QUOTED_STRING" to "\"hello world\"", tokens[0])
    }

    @Test
    fun testDoubleQuotedStringWithEscapes() {
        val tokens = tokenize("\"line1\\nline2\"")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.DOUBLE_QUOTED_STRING" to "\"line1\\nline2\"", tokens[0])
    }

    @Test
    fun testKeywords() {
        val keywords = listOf("if", "elif", "else", "while", "for", "try", "catch", "finally",
            "break", "continue", "return", "fn", "var", "set", "tmp", "del", "use", "pragma", "and", "or", "coalesce")
        for (keyword in keywords) {
            val tokens = tokenize(keyword)
            assertEquals("Expected single token for $keyword", 1, tokens.size)
            assertEquals("Token should match keyword uppercase", "ElvishTokenType.${keyword.uppercase()}", tokens[0].first)
        }
    }

    @Test
    fun testIdentifier() {
        val tokens = tokenize("myfunction")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.IDENTIFIER" to "myfunction", tokens[0])
    }

    @Test
    fun testIdentifierWithHyphen() {
        val tokens = tokenize("my-function")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.IDENTIFIER" to "my-function", tokens[0])
    }

    @Test
    fun testVariable() {
        val tokens = tokenize("\$myvar")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.VARIABLE" to "\$myvar", tokens[0])
    }

    @Test
    fun testExplodeVariable() {
        val tokens = tokenize("\$@args")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.VARIABLE" to "\$@args", tokens[0])
    }

    @Test
    fun testNamespacedVariable() {
        val tokens = tokenize("\$str:join")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.VARIABLE" to "\$str:join", tokens[0])
    }

    @Test
    fun testConstants() {
        assertEquals("ElvishTokenType.TRUE" to "\$true", tokenize("\$true")[0])
        assertEquals("ElvishTokenType.FALSE" to "\$false", tokenize("\$false")[0])
        assertEquals("ElvishTokenType.NIL" to "\$nil", tokenize("\$nil")[0])
    }

    @Test
    fun testInteger() {
        val tokens = tokenize("42")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.INTEGER" to "42", tokens[0])
    }

    @Test
    fun testNegativeInteger() {
        val tokens = tokenize("-17")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.INTEGER" to "-17", tokens[0])
    }

    @Test
    fun testFloat() {
        val tokens = tokenize("3.14159")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.FLOAT" to "3.14159", tokens[0])
    }

    @Test
    fun testScientificNotation() {
        val tokens = tokenize("1.5e10")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.FLOAT" to "1.5e10", tokens[0])
    }

    @Test
    fun testHexNumber() {
        val tokens = tokenize("0xDEADBEEF")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.HEX_NUMBER" to "0xDEADBEEF", tokens[0])
    }

    @Test
    fun testOctalNumber() {
        val tokens = tokenize("0o755")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.OCTAL_NUMBER" to "0o755", tokens[0])
    }

    @Test
    fun testBinaryNumber() {
        val tokens = tokenize("0b101010")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.BINARY_NUMBER" to "0b101010", tokens[0])
    }

    @Test
    fun testRationalNumber() {
        val tokens = tokenize("22/7")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.FLOAT" to "22/7", tokens[0])
    }

    @Test
    fun testOperators() {
        assertEquals("ElvishTokenType.PIPE" to "|", tokenize("|")[0])
        assertEquals("ElvishTokenType.PLUS" to "+", tokenize("+")[0])
        assertEquals("ElvishTokenType.MINUS" to "-", tokenize("- ")[0])  // Space prevents negative number
        assertEquals("ElvishTokenType.STAR" to "*", tokenize("*")[0])
        assertEquals("ElvishTokenType.SLASH" to "/", tokenize("/")[0])
        assertEquals("ElvishTokenType.PERCENT" to "%", tokenize("%")[0])
    }

    @Test
    fun testComparisonOperators() {
        assertEquals("ElvishTokenType.EQ" to "==", tokenize("==")[0])
        assertEquals("ElvishTokenType.NE" to "!=", tokenize("!=")[0])
        assertEquals("ElvishTokenType.LT" to "<", tokenize("<")[0])
        assertEquals("ElvishTokenType.GT" to ">", tokenize(">")[0])
        assertEquals("ElvishTokenType.LE" to "<=", tokenize("<=")[0])
        assertEquals("ElvishTokenType.GE" to ">=", tokenize(">=")[0])
    }

    @Test
    fun testAssignment() {
        val tokens = tokenize("=")
        assertEquals(1, tokens.size)
        assertEquals("ElvishTokenType.ASSIGN" to "=", tokens[0])
    }

    @Test
    fun testRangeOperator() {
        assertEquals("ElvishTokenType.RANGE" to "..", tokenize("..")[0])
    }

    @Test
    fun testRangeInclusiveOperator() {
        assertEquals("ElvishTokenType.RANGE_INCLUSIVE" to "..=", tokenize("..=")[0])
    }

    @Test
    fun testPunctuation() {
        assertEquals("ElvishTokenType.LPAREN" to "(", tokenize("(")[0])
        assertEquals("ElvishTokenType.RPAREN" to ")", tokenize(")")[0])
        assertEquals("ElvishTokenType.LBRACKET" to "[", tokenize("[")[0])
        assertEquals("ElvishTokenType.RBRACKET" to "]", tokenize("]")[0])
        assertEquals("ElvishTokenType.LBRACE" to "{", tokenize("{")[0])
        assertEquals("ElvishTokenType.RBRACE" to "}", tokenize("}")[0])
        assertEquals("ElvishTokenType.SEMICOLON" to ";", tokenize(";")[0])
        assertEquals("ElvishTokenType.AMPERSAND" to "&", tokenize("&")[0])
        assertEquals("ElvishTokenType.COLON" to ":", tokenize(":")[0])
        assertEquals("ElvishTokenType.COMMA" to ",", tokenize(",")[0])
    }

    @Test
    fun testWhitespace() {
        val tokens = tokenize("foo   bar")
        assertEquals(3, tokens.size)
        assertEquals("ElvishTokenType.IDENTIFIER" to "foo", tokens[0])
        assertEquals("ElvishTokenType.WHITE_SPACE" to "   ", tokens[1])
        assertEquals("ElvishTokenType.IDENTIFIER" to "bar", tokens[2])
    }

    @Test
    fun testNewline() {
        val tokens = tokenize("foo\nbar")
        assertEquals(3, tokens.size)
        assertEquals("ElvishTokenType.IDENTIFIER" to "foo", tokens[0])
        assertEquals("ElvishTokenType.NEWLINE" to "\n", tokens[1])
        assertEquals("ElvishTokenType.IDENTIFIER" to "bar", tokens[2])
    }

    @Test
    fun testComplexExpression() {
        val tokens = tokenize("if \$x == 42 { echo \"hello\" }")
        // if, space, $x, space, ==, space, 42, space, {, space, echo, space, "hello", space, }
        val expectedTypes = listOf(
            "ElvishTokenType.IF",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.VARIABLE",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.EQ",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.INTEGER",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.LBRACE",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.IDENTIFIER",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.DOUBLE_QUOTED_STRING",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.RBRACE"
        )
        assertEquals(expectedTypes.size, tokens.size)
        for (i in expectedTypes.indices) {
            assertEquals("Token $i type mismatch", expectedTypes[i], tokens[i].first)
        }
    }

    @Test
    fun testFunctionDefinition() {
        val tokens = tokenize("fn greet {|name| echo \$name }")
        val expectedTypes = listOf(
            "ElvishTokenType.FN",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.IDENTIFIER",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.LBRACE",
            "ElvishTokenType.PIPE",
            "ElvishTokenType.IDENTIFIER",
            "ElvishTokenType.PIPE",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.IDENTIFIER",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.VARIABLE",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.RBRACE"
        )
        assertEquals(expectedTypes.size, tokens.size)
        for (i in expectedTypes.indices) {
            assertEquals("Token $i type mismatch", expectedTypes[i], tokens[i].first)
        }
    }

    @Test
    fun testVariableDeclaration() {
        val tokens = tokenize("var x = 5")
        val expectedTypes = listOf(
            "ElvishTokenType.VAR",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.IDENTIFIER",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.ASSIGN",
            "ElvishTokenType.WHITE_SPACE",
            "ElvishTokenType.INTEGER"
        )
        assertEquals(expectedTypes.size, tokens.size)
        for (i in expectedTypes.indices) {
            assertEquals("Token $i type mismatch", expectedTypes[i], tokens[i].first)
        }
    }

    @Test
    fun testEmptyInput() {
        val tokens = tokenize("")
        assertEquals(0, tokens.size)
    }

    @Test
    fun testLexerState() {
        val lexer = ElvishLexer()
        lexer.start("test", 0, 4, 0)
        assertEquals(0, lexer.state)
    }

    @Test
    fun testBufferAccessors() {
        val text = "hello world"
        val lexer = ElvishLexer()
        lexer.start(text, 0, text.length, 0)
        assertEquals(text, lexer.bufferSequence.toString())
        assertEquals(text.length, lexer.bufferEnd)
    }

    @Test
    fun testPartialBuffer() {
        val text = "hello world"
        val lexer = ElvishLexer()
        lexer.start(text, 0, 5, 0) // Only "hello"
        assertEquals(text, lexer.bufferSequence.toString())
        assertEquals(5, lexer.bufferEnd)
        assertEquals("hello", text.substring(lexer.tokenStart, lexer.tokenEnd))
        lexer.advance()
        assertNull(lexer.tokenType)
    }
}
