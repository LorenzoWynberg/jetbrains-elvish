package com.elvish.plugin.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * Basic settings editor for Elvish run configuration.
 * Provides UI for editing script path (will be enhanced in STORY-6.4.2).
 */
class ElvishRunConfigurationEditor(private val project: Project) : SettingsEditor<ElvishRunConfiguration>() {

    private val scriptPathField = TextFieldWithBrowseButton()

    init {
        scriptPathField.addBrowseFolderListener(
            "Select Elvish Script",
            "Select the .elv script to run",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor("elv")
        )
    }

    override fun resetEditorFrom(config: ElvishRunConfiguration) {
        scriptPathField.text = config.scriptPath
    }

    override fun applyEditorTo(config: ElvishRunConfiguration) {
        config.scriptPath = scriptPathField.text
    }

    override fun createEditor(): JComponent = panel {
        row("Script:") {
            cell(scriptPathField)
                .align(AlignX.FILL)
        }
        row {
            comment("Additional settings (arguments, working directory, environment) will be added in STORY-6.4.2")
        }
    }

    override fun disposeEditor() {
        scriptPathField.dispose()
    }
}
