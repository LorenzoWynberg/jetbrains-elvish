package com.elvish.plugin.run

import com.elvish.plugin.ElvishIcons
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.io.File
import javax.swing.Icon

/**
 * Run configuration for Elvish scripts.
 * Stores: script path, arguments, working directory, environment variables.
 */
class ElvishRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<ElvishRunConfigurationOptions>(project, factory, name) {

    override fun getOptions(): ElvishRunConfigurationOptions {
        return super.getOptions() as ElvishRunConfigurationOptions
    }

    var scriptPath: String
        get() = options.scriptPath
        set(value) {
            options.scriptPath = value
        }

    var scriptArguments: String
        get() = options.scriptArguments
        set(value) {
            options.scriptArguments = value
        }

    var workingDirectory: String
        get() = options.workingDirectory
        set(value) {
            options.workingDirectory = value
        }

    var environmentVariables: Map<String, String>
        get() = options.environmentVariables
        set(value) {
            options.environmentVariables = value.toMutableMap()
        }

    var passParentEnvs: Boolean
        get() = options.passParentEnvs
        set(value) {
            options.passParentEnvs = value
        }

    var useElvishFromSettings: Boolean
        get() = options.useElvishFromSettings
        set(value) {
            options.useElvishFromSettings = value
        }

    var customElvishPath: String
        get() = options.customElvishPath
        set(value) {
            options.customElvishPath = value
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return ElvishRunConfigurationEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        // Will be implemented in STORY-6.4.3
        return null
    }

    override fun checkConfiguration() {
        if (scriptPath.isBlank()) {
            throw RuntimeConfigurationError("Script path is not specified")
        }

        val file = File(scriptPath)
        if (!file.exists()) {
            throw RuntimeConfigurationError("Script file does not exist: $scriptPath")
        }

        if (!scriptPath.endsWith(".elv")) {
            throw RuntimeConfigurationWarning("Script file should have .elv extension")
        }
    }

    override fun getIcon(): Icon = ElvishIcons.FILE
}
