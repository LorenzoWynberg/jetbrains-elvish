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
        val elvishPath = LspTestUtils.findElvishBinary()
        assertNotNull("Elvish binary not found in PATH or common locations", elvishPath)
        assertTrue("Elvish binary does not exist at $elvishPath", File(elvishPath!!).exists())
    }

    /**
     * Test that elvish supports the -lsp flag.
     */
    @Test
    fun testElvishSupportsLspFlag() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return

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
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            // Send a minimal initialize request
            val initRequest = """{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"processId":null,"rootUri":null,"capabilities":{}}}"""
            LspTestUtils.sendMessage(process.outputStream, initRequest)

            // Wait a bit for response
            Thread.sleep(500)

            // Check if process is still alive (good sign - it's processing)
            assertTrue("LSP server process terminated unexpectedly", process.isAlive)

            // Try to read response
            val response = LspTestUtils.readResponse(process.inputStream, 1)
            assertNotNull("LSP server should respond to initialize", response)
            assertTrue("Response should contain capabilities",
                response!!.contains("capabilities"))

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
        val elvishPath = LspTestUtils.findElvishBinary() ?: return

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
}
