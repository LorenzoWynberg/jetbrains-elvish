package com.elvish.plugin.lsp

import com.elvish.plugin.settings.ElvishSettings
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor

class ElvishLspServerDescriptor(project: Project) :
    ProjectWideLspServerDescriptor(project, "Elvish") {

    override fun isSupportedFile(file: VirtualFile): Boolean {
        return file.extension == "elv"
    }

    override fun createCommandLine(): GeneralCommandLine {
        val elvishPath = getElvishPath()
        val commandLine = GeneralCommandLine(elvishPath, "-lsp")
        LOG.info("Creating LSP command line: ${commandLine.commandLineString}")
        return commandLine
    }

    private fun getElvishPath(): String {
        val settings = ElvishSettings.getInstance(project)
        val path = settings.elvishPath
        return if (path.isNotBlank()) path else DEFAULT_ELVISH_PATH
    }

    companion object {
        private val LOG = Logger.getInstance(ElvishLspServerDescriptor::class.java)
        const val DEFAULT_ELVISH_PATH = "elvish"
    }
}
