package com.elvish.plugin.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter

class ElvishLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerStarter
    ) {
        val isElvishFile = file.extension == "elv"
        LOG.info("fileOpened: ${file.path} (isElvishFile=$isElvishFile)")

        if (isElvishFile) {
            LOG.info("Starting Elvish LSP server for project: ${project.name}")
            serverStarter.ensureServerStarted(ElvishLspServerDescriptor(project))
        }
    }

    companion object {
        private val LOG = Logger.getInstance(ElvishLspServerSupportProvider::class.java)
    }
}
