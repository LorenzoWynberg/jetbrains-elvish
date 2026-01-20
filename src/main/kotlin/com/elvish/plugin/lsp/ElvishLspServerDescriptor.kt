package com.elvish.plugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor

class ElvishLspServerDescriptor(project: Project) :
    ProjectWideLspServerDescriptor(project, "Elvish") {

    override fun isSupportedFile(file: VirtualFile): Boolean {
        return file.extension == "elv"
    }

    override fun createCommandLine(): GeneralCommandLine {
        return GeneralCommandLine("elvish", "-lsp")
    }
}
