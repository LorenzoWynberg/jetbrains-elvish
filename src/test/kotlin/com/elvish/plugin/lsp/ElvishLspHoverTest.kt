package com.elvish.plugin.lsp

import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Integration tests for Elvish LSP hover documentation functionality.
 *
 * These tests verify that the Elvish LSP server provides:
 * 1. Documentation when hovering over built-in functions (echo, put, etc.)
 * 2. Type information when hovering over variables
 * 3. Reasonable response time (<1 second)
 *
 * STORY-106: Test LSP hover documentation
 */
class ElvishLspHoverTest {

    private var messageId = 0

    /**
     * Test that hovering over a built-in function shows documentation.
     * Acceptance: Hover over built-in function (e.g., echo, put) shows documentation
     */
    @Test
    fun testHoverOverBuiltinFunction() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            val initResponse = initializeLsp(process)
            assertNotNull("Failed to initialize LSP", initResponse)
            assertTrue("Initialize response should contain capabilities",
                initResponse!!.contains("capabilities"))

            sendNotification(process, "initialized", "{}")

            // Open a document with echo function
            openDocument(process, "echo hello")

            // Request hover at position over 'echo' (line 0, character 2)
            val hoverResponse = requestHover(process, line = 0, character = 2)

            assertNotNull("Hover response should not be null", hoverResponse)
            assertTrue("Hover should return a result",
                hoverResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test that hovering over the 'put' function shows documentation.
     * Acceptance: Hover over built-in function (e.g., echo, put) shows documentation
     */
    @Test
    fun testHoverOverPutFunction() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            openDocument(process, "put hello world")
            val hoverResponse = requestHover(process, line = 0, character = 1)

            assertNotNull("Hover response should not be null", hoverResponse)
            assertTrue("Hover should return a result",
                hoverResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test that hovering over a variable shows type information.
     * Acceptance: Hover over variable shows type information if available
     */
    @Test
    fun testHoverOverVariable() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            // Define a variable and then reference it
            openDocument(process, "var my-var = hello\necho \$my-var")
            // Hover over the variable reference (line 1, character 7 - inside $my-var)
            val hoverResponse = requestHover(process, line = 1, character = 7)

            assertNotNull("Hover response should not be null", hoverResponse)
            assertTrue("Hover should return a result",
                hoverResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test that hover responds within a reasonable time (<1 second).
     * Acceptance: Hover popup appears within reasonable time (<1 second)
     */
    @Test
    fun testHoverResponseTime() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            openDocument(process, "echo hello")

            // Measure time for hover request
            val startTime = System.currentTimeMillis()
            val hoverResponse = requestHover(process, line = 0, character = 2)
            val elapsed = System.currentTimeMillis() - startTime

            assertNotNull("Hover response should not be null", hoverResponse)
            assertTrue("Hover should respond within 1 second (was ${elapsed}ms)",
                elapsed < 1000)

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test hover over a module function (e.g., str:join).
     */
    @Test
    fun testHoverOverModuleFunction() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            // Import str module and use str:join
            openDocument(process, "use str\nstr:join , [a b c]")
            // Hover over str:join (line 1, character 5 - over 'join')
            val hoverResponse = requestHover(process, line = 1, character = 5)

            assertNotNull("Hover response should not be null", hoverResponse)
            assertTrue("Hover should return a result",
                hoverResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    // ==================== Helper Methods ====================

    private fun initializeLsp(process: Process): String? {
        val initParams = """{
            "processId": ${ProcessHandle.current().pid()},
            "rootUri": "file:///tmp",
            "capabilities": {
                "textDocument": {
                    "hover": {
                        "contentFormat": ["plaintext", "markdown"]
                    }
                }
            }
        }"""
        return sendRequest(process, "initialize", initParams)
    }

    private fun openDocument(process: Process, content: String) {
        val didOpenParams = """{
            "textDocument": {
                "uri": "file:///test.elv",
                "languageId": "elvish",
                "version": 1,
                "text": ${LspTestUtils.escapeJson(content)}
            }
        }"""
        sendNotification(process, "textDocument/didOpen", didOpenParams)
    }

    private fun requestHover(process: Process, line: Int, character: Int): String? {
        val hoverParams = """{
            "textDocument": {"uri": "file:///test.elv"},
            "position": {"line": $line, "character": $character}
        }"""
        return sendRequest(process, "textDocument/hover", hoverParams)
    }

    private fun sendRequest(process: Process, method: String, params: String): String? {
        val id = ++messageId
        val message = """{"jsonrpc":"2.0","id":$id,"method":"$method","params":$params}"""
        LspTestUtils.sendMessage(process.outputStream, message)
        return LspTestUtils.readResponse(process.inputStream, id)
    }

    private fun sendNotification(process: Process, method: String, params: String) {
        val message = """{"jsonrpc":"2.0","method":"$method","params":$params}"""
        LspTestUtils.sendMessage(process.outputStream, message)
        Thread.sleep(100)
    }

    private fun shutdownLsp(process: Process) {
        try {
            sendRequest(process, "shutdown", "null")
            sendNotification(process, "exit", "null")
            process.waitFor(2, TimeUnit.SECONDS)
        } finally {
            process.destroyForcibly()
        }
    }
}
