package com.elvish.plugin.parser

import com.elvish.plugin.ElvishLanguage
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType

/**
 * Element types for the Elvish PSI tree (AST nodes).
 * These define the structural elements produced by the parser.
 */
object ElvishElementTypes {
    // File element type - the root of the PSI tree
    @JvmField
    val FILE = IFileElementType("ELVISH_FILE", ElvishLanguage.INSTANCE)

    // Top-level constructs
    @JvmField
    val STATEMENT = ElvishElementType("STATEMENT")

    @JvmField
    val CHUNK = ElvishElementType("CHUNK")

    // Declarations
    @JvmField
    val FUNCTION_DEFINITION = ElvishElementType("FUNCTION_DEFINITION")

    @JvmField
    val VARIABLE_DECLARATION = ElvishElementType("VARIABLE_DECLARATION")

    @JvmField
    val ASSIGNMENT = ElvishElementType("ASSIGNMENT")

    // Control flow
    @JvmField
    val IF_STATEMENT = ElvishElementType("IF_STATEMENT")

    @JvmField
    val ELIF_CLAUSE = ElvishElementType("ELIF_CLAUSE")

    @JvmField
    val ELSE_CLAUSE = ElvishElementType("ELSE_CLAUSE")

    @JvmField
    val WHILE_STATEMENT = ElvishElementType("WHILE_STATEMENT")

    @JvmField
    val FOR_STATEMENT = ElvishElementType("FOR_STATEMENT")

    @JvmField
    val TRY_STATEMENT = ElvishElementType("TRY_STATEMENT")

    @JvmField
    val CATCH_CLAUSE = ElvishElementType("CATCH_CLAUSE")

    @JvmField
    val FINALLY_CLAUSE = ElvishElementType("FINALLY_CLAUSE")

    // Expressions
    @JvmField
    val EXPRESSION = ElvishElementType("EXPRESSION")

    @JvmField
    val COMMAND = ElvishElementType("COMMAND")

    @JvmField
    val PIPELINE = ElvishElementType("PIPELINE")

    @JvmField
    val BINARY_EXPRESSION = ElvishElementType("BINARY_EXPRESSION")

    @JvmField
    val INDEXING = ElvishElementType("INDEXING")

    // Literals and values
    @JvmField
    val STRING_LITERAL = ElvishElementType("STRING_LITERAL")

    @JvmField
    val NUMBER_LITERAL = ElvishElementType("NUMBER_LITERAL")

    @JvmField
    val LIST_LITERAL = ElvishElementType("LIST_LITERAL")

    @JvmField
    val MAP_LITERAL = ElvishElementType("MAP_LITERAL")

    // Other constructs
    @JvmField
    val BLOCK = ElvishElementType("BLOCK")

    @JvmField
    val PARAMETER_LIST = ElvishElementType("PARAMETER_LIST")

    @JvmField
    val ARGUMENT_LIST = ElvishElementType("ARGUMENT_LIST")

    @JvmField
    val VARIABLE_REFERENCE = ElvishElementType("VARIABLE_REFERENCE")

    @JvmField
    val USE_DIRECTIVE = ElvishElementType("USE_DIRECTIVE")

    @JvmField
    val PRAGMA_DIRECTIVE = ElvishElementType("PRAGMA_DIRECTIVE")
}

/**
 * Custom IElementType for Elvish AST nodes.
 * Associates all elements with the Elvish language.
 */
class ElvishElementType(debugName: String) : IElementType(debugName, ElvishLanguage.INSTANCE) {
    override fun toString(): String = "ElvishElementType.${super.toString()}"
}
