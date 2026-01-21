package com.elvish.plugin.editor.structure

import com.elvish.plugin.ElvishFile
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

/**
 * Structure view model for Elvish files.
 * Controls what elements appear in the structure view and how they're displayed.
 */
class ElvishStructureViewModel(
    psiFile: PsiFile,
    editor: Editor?
) : StructureViewModelBase(psiFile, editor, ElvishStructureViewElement(psiFile)),
    StructureViewModel.ElementInfoProvider {

    override fun getSorters(): Array<Sorter> {
        return arrayOf(Sorter.ALPHA_SORTER)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        // All our elements (functions, variables, imports) are leaves
        return element !is ElvishStructureViewElement ||
               (element.value !is PsiFile && element.value !is ElvishFile)
    }
}
