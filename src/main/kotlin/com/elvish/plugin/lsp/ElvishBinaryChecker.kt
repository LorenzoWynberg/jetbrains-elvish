package com.elvish.plugin.lsp

import com.elvish.plugin.settings.ElvishSettings
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility to check if the Elvish binary is available.
 * Caches results per project to avoid repeated filesystem checks.
 */
object ElvishBinaryChecker {
    private val LOG = Logger.getInstance(ElvishBinaryChecker::class.java)

    // Cache of project hash to (path, isAvailable) to avoid repeated checks
    // Key is the configured path, value is whether it was found
    private val availabilityCache = ConcurrentHashMap<String, Boolean>()

    /**
     * Check if the Elvish binary is available for the given project.
     *
     * @param project The project to check settings for
     * @return true if the binary exists and is executable, false otherwise
     */
    fun isElvishAvailable(project: Project): Boolean {
        val settings = ElvishSettings.getInstance(project)
        val path = settings.elvishPath.ifBlank { ElvishLspServerDescriptor.DEFAULT_ELVISH_PATH }

        return availabilityCache.getOrPut(path) {
            checkBinaryExists(path)
        }
    }

    /**
     * Get the configured Elvish path for a project.
     */
    fun getElvishPath(project: Project): String {
        val settings = ElvishSettings.getInstance(project)
        return settings.elvishPath.ifBlank { ElvishLspServerDescriptor.DEFAULT_ELVISH_PATH }
    }

    /**
     * Clear the cache for a specific path. Call this when settings change.
     */
    fun clearCache(path: String) {
        availabilityCache.remove(path)
    }

    /**
     * Clear all cached results.
     */
    fun clearAllCache() {
        availabilityCache.clear()
    }

    private fun checkBinaryExists(path: String): Boolean {
        // First, check if it's an absolute path
        val file = File(path)
        if (file.isAbsolute) {
            val exists = file.exists() && file.canExecute()
            LOG.info("Checking absolute path '$path': exists=$exists")
            return exists
        }

        // Otherwise, search in PATH
        val pathEnv = System.getenv("PATH") ?: ""
        val pathSeparator = File.pathSeparator
        val pathDirs = pathEnv.split(pathSeparator)

        for (dir in pathDirs) {
            val candidate = File(dir, path)
            if (candidate.exists() && candidate.canExecute()) {
                LOG.info("Found '$path' in PATH at: ${candidate.absolutePath}")
                return true
            }
        }

        // Also check common installation locations on macOS/Linux
        val commonLocations = listOf(
            "/usr/local/bin/$path",
            "/opt/homebrew/bin/$path",
            "/usr/bin/$path",
            "/bin/$path",
            "${System.getProperty("user.home")}/.local/bin/$path",
            "${System.getProperty("user.home")}/go/bin/$path"
        )

        for (location in commonLocations) {
            val candidate = File(location)
            if (candidate.exists() && candidate.canExecute()) {
                LOG.info("Found '$path' at common location: $location")
                return true
            }
        }

        LOG.warn("Elvish binary '$path' not found in PATH or common locations")
        return false
    }
}
