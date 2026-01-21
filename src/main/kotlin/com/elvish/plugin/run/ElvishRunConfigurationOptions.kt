package com.elvish.plugin.run

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

/**
 * Options (state) for Elvish run configuration.
 * Stores configuration properties that persist between IDE sessions.
 */
class ElvishRunConfigurationOptions : RunConfigurationOptions() {

    private val scriptPathProperty: StoredProperty<String?> = string("").provideDelegate(this, "scriptPath")
    private val scriptArgumentsProperty: StoredProperty<String?> = string("").provideDelegate(this, "scriptArguments")
    private val workingDirectoryProperty: StoredProperty<String?> = string("").provideDelegate(this, "workingDirectory")
    private val passParentEnvsProperty: StoredProperty<Boolean> = property(true).provideDelegate(this, "passParentEnvs")

    // Environment variables stored as semicolon-separated key=value pairs
    private val environmentVariablesProperty: StoredProperty<String?> = string("").provideDelegate(this, "environmentVariables")

    var scriptPath: String
        get() = scriptPathProperty.getValue(this) ?: ""
        set(value) {
            scriptPathProperty.setValue(this, value)
        }

    var scriptArguments: String
        get() = scriptArgumentsProperty.getValue(this) ?: ""
        set(value) {
            scriptArgumentsProperty.setValue(this, value)
        }

    var workingDirectory: String
        get() = workingDirectoryProperty.getValue(this) ?: ""
        set(value) {
            workingDirectoryProperty.setValue(this, value)
        }

    var passParentEnvs: Boolean
        get() = passParentEnvsProperty.getValue(this)
        set(value) {
            passParentEnvsProperty.setValue(this, value)
        }

    var environmentVariables: MutableMap<String, String>
        get() {
            val stored = environmentVariablesProperty.getValue(this) ?: ""
            if (stored.isBlank()) return mutableMapOf()
            return stored.split(";")
                .filter { it.contains("=") }
                .associate {
                    val parts = it.split("=", limit = 2)
                    parts[0] to parts.getOrElse(1) { "" }
                }
                .toMutableMap()
        }
        set(value) {
            environmentVariablesProperty.setValue(
                this,
                value.entries.joinToString(";") { "${it.key}=${it.value}" }
            )
        }
}
