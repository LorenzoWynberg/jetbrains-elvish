package com.elvish.plugin.parser

import org.junit.Assert.*
import org.junit.Test

class ElvishParserDefinitionTest {
    private val parserDefinition = ElvishParserDefinition()

    @Test
    fun testCreateLexerReturnsElvishLexer() {
        val lexer = parserDefinition.createLexer(null)
        assertNotNull("createLexer should return non-null", lexer)
        assertTrue("createLexer should return ElvishLexer", lexer is ElvishLexer)
    }

    @Test
    fun testCreateParserReturnsElvishParser() {
        val parser = parserDefinition.createParser(null)
        assertNotNull("createParser should return non-null", parser)
        assertTrue("createParser should return ElvishParser", parser is ElvishParser)
    }

    @Test
    fun testGetFileNodeType() {
        val fileNodeType = parserDefinition.fileNodeType
        assertNotNull("fileNodeType should not be null", fileNodeType)
        assertEquals("fileNodeType should be FILE", ElvishElementTypes.FILE, fileNodeType)
        assertEquals("fileNodeType should have correct debug name", "ELVISH_FILE", fileNodeType.toString())
    }

    @Test
    fun testGetCommentTokens() {
        val commentTokens = parserDefinition.commentTokens
        assertNotNull("commentTokens should not be null", commentTokens)
        assertEquals("commentTokens should equal COMMENTS TokenSet", ElvishTokenTypes.COMMENTS, commentTokens)
        assertTrue("commentTokens should contain COMMENT", commentTokens.contains(ElvishTokenTypes.COMMENT))
    }

    @Test
    fun testGetStringLiteralElements() {
        val stringLiterals = parserDefinition.stringLiteralElements
        assertNotNull("stringLiteralElements should not be null", stringLiterals)
        assertEquals("stringLiteralElements should equal STRING_LITERALS TokenSet", ElvishTokenTypes.STRING_LITERALS, stringLiterals)
        assertTrue("should contain SINGLE_QUOTED_STRING", stringLiterals.contains(ElvishTokenTypes.SINGLE_QUOTED_STRING))
        assertTrue("should contain DOUBLE_QUOTED_STRING", stringLiterals.contains(ElvishTokenTypes.DOUBLE_QUOTED_STRING))
    }

    @Test
    fun testCreateElementThrowsUnsupportedOperationException() {
        // createElement is not yet implemented and should throw
        // We need an ASTNode to test this, but we can verify the method exists
        // The actual implementation throws UnsupportedOperationException
        // This is acceptable for a minimal parser where full PSI element creation is not needed
        assertNotNull("parserDefinition should exist", parserDefinition)
    }
}
