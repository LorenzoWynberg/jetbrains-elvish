package com.elvish.plugin.parser

import com.intellij.psi.tree.IElementType
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for ElvishParser.
 * Verifies that the parser correctly creates AST structures for various Elvish constructs.
 *
 * These tests use a lightweight marker-tracking approach to verify parser behavior
 * without requiring the full IntelliJ platform test framework.
 */
class ElvishParserTest {

    private fun parse(text: String): TestParseResult {
        val lexer = ElvishLexer()
        val parser = ElvishParser()
        val testBuilder = SimpleTestPsiBuilder(text, lexer)
        parser.parse(ElvishElementTypes.FILE, testBuilder)
        return TestParseResult(testBuilder.markers, testBuilder.errors)
    }

    @Test
    fun testEmptyFile() {
        val result = parse("")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FILE })
    }

    @Test
    fun testSimpleCommand() {
        val result = parse("echo hello")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testVariableDeclaration() {
        val result = parse("var x = 5")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.VARIABLE_DECLARATION })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testSetStatement() {
        val result = parse("set x = 10")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.ASSIGNMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testFunctionDefinition() {
        val result = parse("fn greet { echo hello }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FUNCTION_DEFINITION })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.BLOCK })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testFunctionWithParameters() {
        val result = parse("fn greet {|name| echo \$name }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FUNCTION_DEFINITION })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.PARAMETER_LIST })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testIfStatement() {
        val result = parse("if \$x { echo yes }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.IF_STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testIfElseStatement() {
        val result = parse("if \$x { echo yes } else { echo no }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.IF_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.ELSE_CLAUSE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testIfElifElseStatement() {
        val result = parse("if \$a { put 1 } elif \$b { put 2 } else { put 3 }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.IF_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.ELIF_CLAUSE })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.ELSE_CLAUSE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testWhileStatement() {
        val result = parse("while \$true { echo loop }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.WHILE_STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testForStatement() {
        val result = parse("for x [1 2 3] { echo \$x }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FOR_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.LIST_LITERAL })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testTryCatchStatement() {
        val result = parse("try { risky } catch e { echo \$e }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.TRY_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.CATCH_CLAUSE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testTryFinallyStatement() {
        val result = parse("try { risky } finally { cleanup }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.TRY_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FINALLY_CLAUSE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testTryCatchFinallyStatement() {
        val result = parse("try { risky } catch e { handle } finally { cleanup }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.TRY_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.CATCH_CLAUSE })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FINALLY_CLAUSE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testPipeline() {
        val result = parse("ls | grep foo | head")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.PIPELINE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testUseDirective() {
        val result = parse("use str")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.USE_DIRECTIVE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testPragmaDirective() {
        val result = parse("pragma unknown-command = disallow")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.PRAGMA_DIRECTIVE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testBinaryExpression() {
        val result = parse("\$x == 5")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.BINARY_EXPRESSION })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testComparisonOperators() {
        for (op in listOf("==", "!=", "<", ">", "<=", ">=")) {
            val result = parse("\$x $op 5")
            assertTrue("Binary expression expected for $op",
                result.markers.any { it.elementType == ElvishElementTypes.BINARY_EXPRESSION })
        }
    }

    @Test
    fun testArithmeticOperators() {
        for (op in listOf("+", "-", "*", "/", "%")) {
            val result = parse("1 $op 2")
            assertTrue("Binary expression expected for $op",
                result.markers.any { it.elementType == ElvishElementTypes.BINARY_EXPRESSION })
        }
    }

    @Test
    fun testLogicalOperators() {
        for (op in listOf("and", "or", "coalesce")) {
            val result = parse("\$a $op \$b")
            assertTrue("Binary expression expected for $op",
                result.markers.any { it.elementType == ElvishElementTypes.BINARY_EXPRESSION })
        }
    }

    @Test
    fun testListLiteral() {
        val result = parse("[1 2 3 4 5]")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.LIST_LITERAL })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testNestedList() {
        val result = parse("[[1 2] [3 4]]")
        assertEquals("Should have 3 list literals (outer + 2 inner)", 3,
            result.markers.count { it.elementType == ElvishElementTypes.LIST_LITERAL })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testStringLiteral() {
        val result = parse("echo \"hello world\"")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STRING_LITERAL })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testSingleQuotedString() {
        val result = parse("echo 'hello world'")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STRING_LITERAL })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testNumberLiterals() {
        for (num in listOf("42", "3.14", "0xFF", "0o77", "0b1010")) {
            val result = parse("put $num")
            assertTrue("Number literal expected for $num",
                result.markers.any { it.elementType == ElvishElementTypes.NUMBER_LITERAL })
        }
    }

    @Test
    fun testVariableReference() {
        val result = parse("echo \$myvar")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.VARIABLE_REFERENCE })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testVariableIndexing() {
        val result = parse("put \$list[0]")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.INDEXING })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testComment() {
        val result = parse("# this is a comment\necho hello")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testMultipleStatements() {
        val result = parse("echo one\necho two\necho three")
        assertEquals("Should have 3 statements", 3,
            result.markers.count { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testSemicolonSeparatedStatements() {
        val result = parse("echo one; echo two; echo three")
        assertEquals("Should have 3 statements", 3,
            result.markers.count { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testBreakStatement() {
        val result = parse("break")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testContinueStatement() {
        val result = parse("continue")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testReturnStatement() {
        val result = parse("return 42")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.STATEMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testTmpDeclaration() {
        val result = parse("tmp x = 5")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.VARIABLE_DECLARATION })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testDelStatement() {
        val result = parse("del x")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.ASSIGNMENT })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testRangeOperator() {
        val result = parse("range 1..10")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.BINARY_EXPRESSION })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testAnonymousFunction() {
        val result = parse("fn {|x| put \$x }")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FUNCTION_DEFINITION })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.PARAMETER_LIST })
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testErrorRecoveryMissingBrace() {
        val result = parse("if \$x { echo yes")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.IF_STATEMENT })
        assertTrue(result.errors.isNotEmpty())
    }

    @Test
    fun testErrorRecoveryUnexpectedToken() {
        val result = parse(")))")
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FILE })
        assertTrue(result.errors.isNotEmpty())
    }

    @Test
    fun testComplexNestedStructure() {
        val code = "fn process {|items| for item \$items { if \$item { echo \$item } } }"
        val result = parse(code)
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FUNCTION_DEFINITION })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.FOR_STATEMENT })
        assertTrue(result.markers.any { it.elementType == ElvishElementTypes.IF_STATEMENT })
        assertTrue(result.errors.isEmpty())
    }
}

/**
 * Data class to hold parser test results.
 */
data class TestParseResult(
    val markers: List<MarkerInfo>,
    val errors: List<String>
)

/**
 * Information about a completed marker.
 */
data class MarkerInfo(
    val elementType: IElementType,
    val start: Int,
    val end: Int
)

/**
 * Minimal PsiBuilder implementation for testing parser logic.
 * Implements only the methods actually used by the parser.
 */
class SimpleTestPsiBuilder(
    private val text: String,
    private val lexer: ElvishLexer
) : com.intellij.lang.PsiBuilder {

    private var currentOffset = 0
    private val markerStack = mutableListOf<SimpleTestMarker>()
    val markers = mutableListOf<MarkerInfo>()
    val errors = mutableListOf<String>()

    init {
        lexer.start(text, 0, text.length, 0)
    }

    override fun getTokenType(): IElementType? = lexer.tokenType

    override fun advanceLexer() {
        currentOffset = lexer.tokenEnd
        lexer.advance()
    }

    override fun mark(): com.intellij.lang.PsiBuilder.Marker {
        val marker = SimpleTestMarker(currentOffset)
        markerStack.add(marker)
        return marker
    }

    override fun eof(): Boolean = lexer.tokenType == null

    override fun getCurrentOffset(): Int = currentOffset

    override fun getOriginalText(): CharSequence = text

    override fun getTreeBuilt(): com.intellij.lang.ASTNode {
        return StubASTNode(text)
    }

    // Stub implementations for interface compliance
    override fun getProject(): com.intellij.openapi.project.Project? = null
    override fun <T : Any?> getUserData(key: com.intellij.openapi.util.Key<T>): T? = null
    override fun <T : Any?> putUserData(key: com.intellij.openapi.util.Key<T>, value: T?) {}
    override fun getTokenText(): String? = if (lexer.tokenType != null)
        text.substring(lexer.tokenStart, lexer.tokenEnd) else null
    override fun rawTokenIndex(): Int = 0
    override fun rawTokenTypeStart(index: Int): Int = 0
    override fun setTokenTypeRemapper(remapper: com.intellij.lang.ITokenTypeRemapper?) {}
    override fun setWhitespaceSkippedCallback(callback: com.intellij.lang.WhitespaceSkippedCallback?) {}
    override fun remapCurrentToken(type: IElementType) {}
    override fun lookAhead(steps: Int): IElementType? = null
    override fun rawLookup(steps: Int): IElementType? = null
    override fun error(message: String) { errors.add(message) }
    override fun setDebugMode(debugMode: Boolean) {}
    override fun enforceCommentTokens(tokens: com.intellij.psi.tree.TokenSet) {}
    override fun getLatestDoneMarker(): com.intellij.lang.LighterASTNode? = null
    override fun getLightTree(): com.intellij.util.diff.FlyweightCapableTreeStructure<com.intellij.lang.LighterASTNode> {
        throw UnsupportedOperationException("Not needed for tests")
    }

    inner class SimpleTestMarker(private val startOffset: Int) : com.intellij.lang.PsiBuilder.Marker {
        override fun done(type: IElementType) {
            markerStack.remove(this)
            markers.add(MarkerInfo(type, startOffset, currentOffset))
        }

        override fun doneBefore(type: IElementType, before: com.intellij.lang.PsiBuilder.Marker) = done(type)
        override fun doneBefore(type: IElementType, before: com.intellij.lang.PsiBuilder.Marker, errorMessage: String) = done(type)
        override fun drop() { markerStack.remove(this) }
        override fun rollbackTo() { markerStack.remove(this) }
        override fun collapse(type: IElementType) = done(type)

        override fun precede(): com.intellij.lang.PsiBuilder.Marker {
            val newMarker = SimpleTestMarker(startOffset)
            markerStack.add(newMarker)
            return newMarker
        }

        override fun error(message: String) {
            markerStack.remove(this)
            errors.add(message)
        }

        override fun setCustomEdgeTokenBinders(
            left: com.intellij.lang.WhitespacesAndCommentsBinder?,
            right: com.intellij.lang.WhitespacesAndCommentsBinder?
        ) {}

        override fun errorBefore(message: String, before: com.intellij.lang.PsiBuilder.Marker) = error(message)
    }
}

/**
 * Stub AST node for test purposes.
 */
class StubASTNode(private val text: String) : com.intellij.lang.ASTNode {
    override fun getElementType(): IElementType = ElvishElementTypes.FILE
    override fun getText(): String = text
    override fun getTextRange(): com.intellij.openapi.util.TextRange =
        com.intellij.openapi.util.TextRange(0, text.length)
    override fun getTextLength(): Int = text.length
    override fun getStartOffset(): Int = 0
    override fun getStartOffsetInParent(): Int = 0
    override fun getTreeParent(): com.intellij.lang.ASTNode? = null
    override fun getFirstChildNode(): com.intellij.lang.ASTNode? = null
    override fun getLastChildNode(): com.intellij.lang.ASTNode? = null
    override fun getTreeNext(): com.intellij.lang.ASTNode? = null
    override fun getTreePrev(): com.intellij.lang.ASTNode? = null
    override fun getChildren(filter: com.intellij.psi.tree.TokenSet?): Array<com.intellij.lang.ASTNode> = emptyArray()
    override fun addChild(child: com.intellij.lang.ASTNode) {}
    override fun addChild(child: com.intellij.lang.ASTNode, anchorBefore: com.intellij.lang.ASTNode?) {}
    override fun addLeaf(leafType: IElementType, leafText: CharSequence, anchorBefore: com.intellij.lang.ASTNode?) {}
    override fun removeChild(child: com.intellij.lang.ASTNode) {}
    override fun removeRange(firstNodeToRemove: com.intellij.lang.ASTNode, firstNodeToKeep: com.intellij.lang.ASTNode?) {}
    override fun replaceChild(oldChild: com.intellij.lang.ASTNode, newChild: com.intellij.lang.ASTNode) {}
    override fun replaceAllChildrenToChildrenOf(anotherParent: com.intellij.lang.ASTNode) {}
    override fun addChildren(firstChild: com.intellij.lang.ASTNode, firstChildToNotAdd: com.intellij.lang.ASTNode?, anchorBefore: com.intellij.lang.ASTNode?) {}
    override fun clone(): Any = this
    override fun copyElement(): com.intellij.lang.ASTNode = this
    override fun findChildByType(type: IElementType): com.intellij.lang.ASTNode? = null
    override fun findChildByType(typesSet: com.intellij.psi.tree.TokenSet): com.intellij.lang.ASTNode? = null
    override fun findChildByType(type: IElementType, anchor: com.intellij.lang.ASTNode?): com.intellij.lang.ASTNode? = null
    override fun findChildByType(typesSet: com.intellij.psi.tree.TokenSet, anchor: com.intellij.lang.ASTNode?): com.intellij.lang.ASTNode? = null
    override fun findLeafElementAt(offset: Int): com.intellij.lang.ASTNode? = null
    override fun <T : Any?> getCopyableUserData(key: com.intellij.openapi.util.Key<T>): T? = null
    override fun <T : Any?> putCopyableUserData(key: com.intellij.openapi.util.Key<T>, value: T?) {}
    override fun <T : Any?> getUserData(key: com.intellij.openapi.util.Key<T>): T? = null
    override fun <T : Any?> putUserData(key: com.intellij.openapi.util.Key<T>, value: T?) {}
    override fun getPsi(): com.intellij.psi.PsiElement? = null
    override fun <T : com.intellij.psi.PsiElement?> getPsi(clazz: Class<T>): T? = null
    override fun textContains(c: Char): Boolean = text.contains(c)
    override fun getChars(): CharSequence = text
}
