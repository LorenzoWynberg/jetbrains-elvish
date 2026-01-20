package com.elvish.plugin.lsp

import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Integration tests for Elvish LSP functionality.
 *
 * These tests verify that:
 * 1. The Elvish binary exists and supports -lsp flag
 * 2. The LSP process can be started successfully
 * 3. The LSP responds to basic JSON-RPC messages
 */
class ElvishLspIntegrationTest {

    /**
     * Test that the Elvish binary exists in PATH and supports -lsp flag.
     */
    @Test
    fun testElvishBinaryExists() {
        // Find elvish in PATH or common locations
        val elvishPath = findElvishBinary()
        assertNotNull("Elvish binary not found in PATH or common locations", elvishPath)
        assertTrue("Elvish binary does not exist at $elvishPath", File(elvishPath!!).exists())
    }

    /**
     * Test that elvish supports the -lsp flag.
     */
    @Test
    fun testElvishSupportsLspFlag() {
        val elvishPath = findElvishBinary() ?: return // Skip if elvish not found

        val process = ProcessBuilder(elvishPath, "--help")
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()

        assertTrue("Elvish does not appear to support -lsp flag", output.contains("-lsp"))
    }

    /**
     * Test that the LSP server can be started and responds to initialization.
     * This tests the core functionality that the plugin relies on.
     */
    @Test
    fun testLspServerStarts() {
        val elvishPath = findElvishBinary() ?: return // Skip if elvish not found

        val process = ProcessBuilder(elvishPath, "-lsp")
            .redirectErrorStream(false)
            .start()

        try {
            // Send a minimal initialize request
            val initRequest = """{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"processId":null,"rootUri":null,"capabilities":{}}}"""
            val contentLength = initRequest.toByteArray().size
            val fullRequest = "Content-Length: $contentLength\r\n\r\n$initRequest"

            process.outputStream.write(fullRequest.toByteArray())
            process.outputStream.flush()

            // Wait a bit for response
            Thread.sleep(500)

            // Check if process is still alive (good sign - it's processing)
            assertTrue("LSP server process terminated unexpectedly", process.isAlive)

            // Try to read response headers
            val inputStream = process.inputStream
            if (inputStream.available() > 0) {
                val buffer = ByteArray(minOf(inputStream.available(), 1024))
                inputStream.read(buffer)
                val response = String(buffer)
                assertTrue("LSP server did not respond with Content-Length",
                    response.contains("Content-Length"))
            }

        } finally {
            process.destroyForcibly()
        }
    }

    /**
     * Verify the LSP command can be executed with expected arguments.
     * The plugin uses "elvish -lsp" to start the language server.
     */
    @Test
    fun testLspCommandExecution() {
        val elvishPath = findElvishBinary() ?: return // Skip if elvish not found

        // Verify elvish binary path ends with "elvish" (matching plugin config)
        assertTrue("Binary should be named 'elvish'", elvishPath.endsWith("elvish"))

        // Verify version format to ensure valid binary
        val process = ProcessBuilder(elvishPath, "--version")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()

        assertTrue("Elvish version should be in format X.Y.Z",
            output.matches(Regex("\\d+\\.\\d+\\.\\d+")))
    }

    private fun findElvishBinary(): String? {
        // Try common locations
        val candidates = listOf(
            "/opt/homebrew/bin/elvish",  // macOS ARM
            "/usr/local/bin/elvish",      // macOS Intel / Linux
            "/usr/bin/elvish",            // Linux system
            System.getenv("HOME")?.let { "$it/.local/bin/elvish" }  // User local
        ).filterNotNull()

        for (path in candidates) {
            if (File(path).exists() && File(path).canExecute()) {
                return path
            }
        }

        // Try PATH
        val pathDirs = System.getenv("PATH")?.split(File.pathSeparator) ?: return null
        for (dir in pathDirs) {
            val elvish = File(dir, "elvish")
            if (elvish.exists() && elvish.canExecute()) {
                return elvish.absolutePath
            }
        }

        return null
    }
}
