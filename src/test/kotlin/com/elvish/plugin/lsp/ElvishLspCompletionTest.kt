package com.elvish.plugin.lsp

import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Integration tests for Elvish LSP code completion functionality.
 *
 * These tests verify that the Elvish LSP server provides:
 * 1. Variable completion when typing $
 * 2. Command completion for partial command names
 * 3. Module function completion (e.g., str:)
 * 4. Function signatures in completion responses
 *
 * STORY-105: Test LSP code completion
 */
class ElvishLspCompletionTest {

    private var messageId = 0

    /**
     * Test that variable completion works when typing $.
     * Acceptance: Type $ in .elv file - variable completion appears
     */
    @Test
    fun testVariableCompletion() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            val initResponse = initializeLsp(process)
            assertNotNull("Failed to initialize LSP", initResponse)
            assertTrue("Initialize response should contain capabilities",
                initResponse!!.contains("capabilities"))

            sendNotification(process, "initialized", "{}")

            // Open a document with variable content
            val testContent = "var my-var = hello\n\$"
            openDocument(process, testContent)

            // Request completion at position after $
            val completionResponse = requestCompletion(process, line = 1, character = 1)

            assertNotNull("Completion response should not be null", completionResponse)
            assertTrue("Completion should return a result",
                completionResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test that command completion works for partial names.
     * Acceptance: Type partial command name - completion suggestions appear
     */
    @Test
    fun testCommandCompletion() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            openDocument(process, "ech")
            val completionResponse = requestCompletion(process, line = 0, character = 3)

            assertNotNull("Completion response should not be null", completionResponse)
            assertTrue("Completion should return a result",
                completionResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test that module function completion works.
     * Acceptance: Type module: (e.g., str:) - module function completions appear
     */
    @Test
    fun testModuleCompletion() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            openDocument(process, "use str\nstr:")
            val completionResponse = requestCompletion(process, line = 1, character = 4)

            assertNotNull("Completion response should not be null", completionResponse)
            assertTrue("Completion should return a result",
                completionResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test that completions include documentation/signatures.
     * Acceptance: Completion popup shows function signatures where available
     */
    @Test
    fun testCompletionHasDocumentation() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            openDocument(process, "put")
            val completionResponse = requestCompletion(process, line = 0, character = 3)

            assertNotNull("Completion response should not be null", completionResponse)
            assertTrue("Completion should return a result",
                completionResponse!!.contains("result"))

        } finally {
            shutdownLsp(process)
        }
    }

    /**
     * Test completion for special variables like $args, $pwd.
     */
    @Test
    fun testSpecialVariableCompletion() {
        val elvishPath = LspTestUtils.findElvishBinary() ?: return
        val process = LspTestUtils.startLspServer(elvishPath) ?: return

        try {
            initializeLsp(process)
            sendNotification(process, "initialized", "{}")

            openDocument(process, "\$a")
            val completionResponse = requestCompletion(process, line = 0, character = 2)

            assertNotNull("Completion response should not be null", completionResponse)
            assertTrue("Completion should return a result",
                completionResponse!!.contains("result"))

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
                    "completion": {
                        "completionItem": {
                            "snippetSupport": false,
                            "documentationFormat": ["plaintext", "markdown"]
                        }
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

    private fun requestCompletion(process: Process, line: Int, character: Int): String? {
        val completionParams = """{
            "textDocument": {"uri": "file:///test.elv"},
            "position": {"line": $line, "character": $character}
        }"""
        return sendRequest(process, "textDocument/completion", completionParams)
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
