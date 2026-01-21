package com.elvish.plugin.run

import com.elvish.plugin.settings.ElvishSettings
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * Settings editor for Elvish run configuration.
 * Provides UI for editing: script path, arguments, working directory,
 * environment variables, and Elvish interpreter path.
 */
class ElvishRunConfigurationEditor(private val project: Project) : SettingsEditor<ElvishRunConfiguration>() {

    private val scriptPathField = TextFieldWithBrowseButton()
    private val scriptArgumentsField = RawCommandLineEditor()
    private val workingDirectoryField = TextFieldWithBrowseButton()
    private val environmentVariablesField = EnvironmentVariablesTextFieldWithBrowseButton()
    private val useElvishFromSettingsCheckBox = JBCheckBox("Use Elvish from project settings")
    private val customElvishPathField = TextFieldWithBrowseButton()

    init {
        // Script file browser
        scriptPathField.addBrowseFolderListener(
            "Select Elvish Script",
            "Select the .elv script to run",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor("elv")
        )

        // Working directory browser
        workingDirectoryField.addBrowseFolderListener(
            "Select Working Directory",
            "Select the working directory for script execution",
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )

        // Custom elvish path browser
        customElvishPathField.addBrowseFolderListener(
            "Select Elvish Executable",
            "Select the elvish binary to use for this configuration",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )

        // Toggle custom path field enabled state based on checkbox
        useElvishFromSettingsCheckBox.addActionListener {
            customElvishPathField.isEnabled = !useElvishFromSettingsCheckBox.isSelected
        }
    }

    override fun resetEditorFrom(config: ElvishRunConfiguration) {
        scriptPathField.text = config.scriptPath
        scriptArgumentsField.text = config.scriptArguments
        workingDirectoryField.text = config.workingDirectory.ifBlank { project.basePath ?: "" }
        environmentVariablesField.envs = config.environmentVariables
        environmentVariablesField.isPassParentEnvs = config.passParentEnvs
        useElvishFromSettingsCheckBox.isSelected = config.useElvishFromSettings
        customElvishPathField.text = config.customElvishPath
        customElvishPathField.isEnabled = !config.useElvishFromSettings
    }

    override fun applyEditorTo(config: ElvishRunConfiguration) {
        config.scriptPath = scriptPathField.text
        config.scriptArguments = scriptArgumentsField.text
        config.workingDirectory = workingDirectoryField.text
        config.environmentVariables = environmentVariablesField.envs
        config.passParentEnvs = environmentVariablesField.isPassParentEnvs
        config.useElvishFromSettings = useElvishFromSettingsCheckBox.isSelected
        config.customElvishPath = customElvishPathField.text
    }

    override fun createEditor(): JComponent = panel {
        row("Script:") {
            cell(scriptPathField)
                .align(AlignX.FILL)
                .comment("Path to the .elv script to execute")
        }
        row("Arguments:") {
            cell(scriptArgumentsField)
                .align(AlignX.FILL)
                .comment("Command-line arguments passed to the script")
        }
        row("Working directory:") {
            cell(workingDirectoryField)
                .align(AlignX.FILL)
                .comment("Directory where the script will be executed (defaults to project root)")
        }
        row("Environment variables:") {
            cell(environmentVariablesField)
                .align(AlignX.FILL)
        }
        separator()
        row {
            cell(useElvishFromSettingsCheckBox)
            comment("Path: ${ElvishSettings.getInstance(project).elvishPath}")
        }
        row("Custom Elvish path:") {
            cell(customElvishPathField)
                .align(AlignX.FILL)
                .comment("Override the Elvish binary for this run configuration only")
        }
    }

    override fun disposeEditor() {
        scriptPathField.dispose()
        workingDirectoryField.dispose()
        customElvishPathField.dispose()
    }
}
