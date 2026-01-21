package com.elvish.plugin.run

import com.elvish.plugin.ElvishFileType
import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.util.function.Function

/**
 * Provides gutter run icons for Elvish script files.
 * Shows a green play icon at line 1 of .elv files to quickly run scripts.
 */
class ElvishRunLineMarkerProvider : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {
        // Only show marker for the first element in Elvish files
        if (!isFirstElementInElvishFile(element)) {
            return null
        }

        // Get run actions for the file
        val actions = ExecutorAction.getActions(0)
        if (actions.isEmpty()) {
            return null
        }

        // Use the newer API with java.util.function.Function for tooltip
        val tooltipProvider = Function<PsiElement, String> { el ->
            "Run ${el.containingFile?.name ?: "Elvish script"}"
        }

        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            actions,
            tooltipProvider
        )
    }

    /**
     * Checks if this element is the first leaf element in an Elvish file.
     * We use this to show the run icon only at the start of the file.
     */
    private fun isFirstElementInElvishFile(element: PsiElement): Boolean {
        val file = element.containingFile ?: return false

        // Check if it's an Elvish file
        if (file.fileType != ElvishFileType) {
            return false
        }

        // Only show marker on the first leaf element in the file
        val firstLeaf = findFirstLeafElement(file)
        return element === firstLeaf
    }

    /**
     * Finds the first leaf element in a PSI file.
     */
    private fun findFirstLeafElement(file: PsiFile): PsiElement? {
        var current: PsiElement? = file.firstChild
        while (current != null && current.firstChild != null) {
            current = current.firstChild
        }
        return current
    }
}
