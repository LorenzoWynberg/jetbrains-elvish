package com.elvish.plugin.editor.structure

import com.elvish.plugin.ElvishFile
import com.elvish.plugin.parser.ElvishElementTypes
import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

/**
 * Represents an element in the structure view tree.
 * Handles both the file root and individual structural elements (functions, variables, imports).
 */
class ElvishStructureViewElement(
    private val element: PsiElement
) : StructureViewTreeElement, SortableTreeElement {

    override fun getValue(): PsiElement = element

    override fun navigate(requestFocus: Boolean) {
        if (element is NavigatablePsiElement) {
            element.navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean {
        return element is NavigatablePsiElement && element.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element is NavigatablePsiElement && element.canNavigateToSource()
    }

    override fun getAlphaSortKey(): String {
        return presentation.presentableText ?: ""
    }

    override fun getPresentation(): ItemPresentation {
        if (element is PsiFile) {
            return element.presentation ?: PresentationData(element.name, null, null, null)
        }
        // For synthetic elements (functions, vars, imports), this is handled by the wrapper
        return PresentationData(element.text, null, null, null)
    }

    override fun getChildren(): Array<TreeElement> {
        if (element !is ElvishFile) {
            return emptyArray()
        }

        val children = mutableListOf<TreeElement>()

        // Find all top-level statements and extract structural elements
        val statements = PsiTreeUtil.getChildrenOfType(element, PsiElement::class.java)
            ?.filter { it.node?.elementType == ElvishElementTypes.STATEMENT }
            ?: emptyList()

        for (statement in statements) {
            val structuralElement = parseStructuralElement(statement)
            if (structuralElement != null) {
                children.add(structuralElement)
            }
        }

        return children.toTypedArray()
    }

    /**
     * Parses a statement to extract structural elements (functions, variables, imports).
     */
    private fun parseStructuralElement(statement: PsiElement): TreeElement? {
        val tokens = collectTokens(statement)
        if (tokens.isEmpty()) return null

        val firstToken = tokens.firstOrNull() ?: return null
        val tokenType = firstToken.node?.elementType

        return when (tokenType) {
            ElvishTokenTypes.FN -> parseFunctionDefinition(statement, tokens)
            ElvishTokenTypes.VAR -> parseVariableDeclaration(statement, tokens)
            ElvishTokenTypes.USE -> parseUseDirective(statement, tokens)
            else -> null
        }
    }

    /**
     * Collects all meaningful tokens from a statement (skipping whitespace).
     */
    private fun collectTokens(statement: PsiElement): List<PsiElement> {
        val tokens = mutableListOf<PsiElement>()
        var child = statement.firstChild

        while (child != null) {
            val tokenType = child.node?.elementType
            if (tokenType != ElvishTokenTypes.WHITE_SPACE &&
                tokenType != ElvishTokenTypes.NEWLINE) {
                tokens.add(child)
            }
            child = child.nextSibling
        }

        return tokens
    }

    /**
     * Parses: fn name {|params| ... } or fn name { ... }
     */
    private fun parseFunctionDefinition(statement: PsiElement, tokens: List<PsiElement>): TreeElement? {
        // tokens[0] = fn
        // tokens[1] = name (identifier)
        // tokens[2+] = block or parameters
        if (tokens.size < 2) return null

        val fnToken = tokens[0]
        val nameToken = tokens.getOrNull(1)

        if (nameToken?.node?.elementType != ElvishTokenTypes.IDENTIFIER) {
            return null
        }

        val functionName = nameToken.text
        val params = extractFunctionParams(tokens)
        val displayText = if (params.isNotEmpty()) {
            "fn $functionName(${params.joinToString(", ")})"
        } else {
            "fn $functionName()"
        }

        return StructuralTreeElement(
            element = fnToken,
            displayText = displayText,
            icon = AllIcons.Nodes.Function,
            navigateTarget = statement
        )
    }

    /**
     * Extracts function parameters from {|param1 param2| ... } syntax.
     */
    private fun extractFunctionParams(tokens: List<PsiElement>): List<String> {
        val params = mutableListOf<String>()

        // Find LBRACE and look for parameter list inside |...|
        var inParams = false
        for (token in tokens) {
            val tokenType = token.node?.elementType

            when (tokenType) {
                ElvishTokenTypes.LBRACE -> {
                    // Start looking for pipe
                }
                ElvishTokenTypes.PIPE -> {
                    if (!inParams) {
                        inParams = true
                    } else {
                        // Second pipe ends params
                        break
                    }
                }
                ElvishTokenTypes.VARIABLE -> {
                    if (inParams) {
                        // Extract variable name without $ prefix
                        val varName = token.text.removePrefix("$").removePrefix("@")
                        params.add(varName)
                    }
                }
                ElvishTokenTypes.IDENTIFIER -> {
                    if (inParams) {
                        params.add(token.text)
                    }
                }
            }
        }

        return params
    }

    /**
     * Parses: var name = value or var name
     */
    private fun parseVariableDeclaration(statement: PsiElement, tokens: List<PsiElement>): TreeElement? {
        // tokens[0] = var
        // tokens[1] = variable ($name) or identifier
        if (tokens.size < 2) return null

        val varToken = tokens[0]
        val nameToken = tokens.getOrNull(1) ?: return null
        val tokenType = nameToken.node?.elementType

        val variableName = when (tokenType) {
            ElvishTokenTypes.VARIABLE -> nameToken.text.removePrefix("$").removePrefix("@")
            ElvishTokenTypes.IDENTIFIER -> nameToken.text
            else -> return null
        }

        return StructuralTreeElement(
            element = varToken,
            displayText = "var $variableName",
            icon = AllIcons.Nodes.Variable,
            navigateTarget = statement
        )
    }

    /**
     * Parses: use module or use module:submodule
     */
    private fun parseUseDirective(statement: PsiElement, tokens: List<PsiElement>): TreeElement? {
        // tokens[0] = use
        // tokens[1+] = module name (may contain colons for submodules)
        if (tokens.size < 2) return null

        val useToken = tokens[0]
        val moduleParts = mutableListOf<String>()

        // Collect all tokens that form the module name (identifiers and colons)
        for (i in 1 until tokens.size) {
            val token = tokens[i]
            val tokenType = token.node?.elementType

            when (tokenType) {
                ElvishTokenTypes.IDENTIFIER -> moduleParts.add(token.text)
                ElvishTokenTypes.COLON -> moduleParts.add(":")
                else -> break // Stop at first non-module token
            }
        }

        if (moduleParts.isEmpty()) return null

        val moduleName = moduleParts.joinToString("")

        return StructuralTreeElement(
            element = useToken,
            displayText = "use $moduleName",
            icon = AllIcons.Nodes.Include,
            navigateTarget = statement
        )
    }
}

/**
 * A synthetic tree element for displaying structural elements with custom presentation.
 */
private class StructuralTreeElement(
    private val element: PsiElement,
    private val displayText: String,
    private val icon: Icon,
    private val navigateTarget: PsiElement
) : StructureViewTreeElement, SortableTreeElement {

    override fun getValue(): PsiElement = navigateTarget

    override fun navigate(requestFocus: Boolean) {
        if (navigateTarget is NavigatablePsiElement) {
            navigateTarget.navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean {
        return navigateTarget is NavigatablePsiElement && navigateTarget.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return navigateTarget is NavigatablePsiElement && navigateTarget.canNavigateToSource()
    }

    override fun getAlphaSortKey(): String = displayText

    override fun getPresentation(): ItemPresentation {
        return PresentationData(displayText, null, icon, null)
    }

    override fun getChildren(): Array<TreeElement> = emptyArray()
}
