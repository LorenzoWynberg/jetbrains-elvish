package com.elvish.plugin.settings

import com.elvish.plugin.lsp.ElvishBinaryChecker
import com.elvish.plugin.lsp.ElvishNotifications
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class ElvishConfigurable(private val project: Project) : Configurable {

    private var elvishPathField: String = ""

    override fun getDisplayName(): String = "Elvish"

    override fun createComponent(): JComponent {
        val settings = ElvishSettings.getInstance(project)
        elvishPathField = settings.elvishPath

        return panel {
            row("Elvish executable:") {
                textFieldWithBrowseButton(
                    FileChooserDescriptorFactory.createSingleFileDescriptor()
                        .withTitle("Select Elvish Executable"),
                    project
                ) { it.path }
                    .bindText(::elvishPathField)
                    .comment("Path to the elvish executable (default: 'elvish' from PATH)")
            }
        }
    }

    override fun isModified(): Boolean {
        val settings = ElvishSettings.getInstance(project)
        return elvishPathField != settings.elvishPath
    }

    override fun apply() {
        val settings = ElvishSettings.getInstance(project)
        val oldPath = settings.elvishPath
        settings.elvishPath = elvishPathField

        // Clear caches when path changes so the binary is re-checked
        if (oldPath != elvishPathField) {
            ElvishBinaryChecker.clearCache(oldPath)
            ElvishBinaryChecker.clearCache(elvishPathField)
            ElvishNotifications.resetNotificationState(project)
        }
    }

    override fun reset() {
        val settings = ElvishSettings.getInstance(project)
        elvishPathField = settings.elvishPath
    }
}
