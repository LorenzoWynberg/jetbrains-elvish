package com.elvish.plugin.editor.structure

import com.elvish.plugin.ElvishFile
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

/**
 * Factory for creating structure view builders for Elvish files.
 * The structure view shows:
 * - Function definitions (fn)
 * - Top-level variable declarations (var)
 * - Module imports (use)
 */
class ElvishStructureViewFactory : PsiStructureViewFactory {

    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        if (psiFile !is ElvishFile) return null

        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return ElvishStructureViewModel(psiFile, editor)
            }
        }
    }
}
