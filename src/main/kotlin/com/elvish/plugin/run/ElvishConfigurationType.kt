package com.elvish.plugin.run

import com.elvish.plugin.ElvishIcons
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

/**
 * Configuration type for running Elvish scripts.
 * Appears in Run > Edit Configurations dialog.
 */
class ElvishConfigurationType : SimpleConfigurationType(
    "ElvishRunConfiguration",
    "Elvish",
    "Run Elvish script",
    NotNullLazyValue.createValue { ElvishIcons.FILE }
) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return ElvishRunConfiguration(project, this, "Elvish")
    }

    override fun getHelpTopic(): String = "reference.dialogs.rundebug.ElvishRunConfiguration"

    override fun isApplicable(project: Project): Boolean = true
}
