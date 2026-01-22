# LSP Integration

Learnings about Language Server Protocol integration.

## Module Dependencies

- `com.intellij.modules.lsp` is the correct module for cross-IDE LSP support
- `com.intellij.modules.ultimate` restricts to IntelliJ IDEA Ultimate only
- As of 2025.3, JetBrains unified distribution makes LSP free for all users
- LSP API is experimental - warnings are expected

## Server Setup

- `LspServerSupportProvider` is the entry point for LSP integration
- `ProjectWideLspServerDescriptor` for single server per project
- `GeneralCommandLine` configures process launch
- `serverStarter.ensureServerStarted` handles lifecycle
- Elvish's built-in LSP uses stdio communication

## LSP Protocol

- Completion: `textDocument/completion` with position
- Hover: `textDocument/hover` with position
- Diagnostics: pushed via `textDocument/publishDiagnostics` notification
- Document must be opened via `textDocument/didOpen` before requests
- Document changes trigger diagnostics via `textDocument/didChange`

## Testing LSP

- `ProcessBuilder` can start and interact with `elvish -lsp`
- LSP server responds to JSON-RPC initialize request
- Use `waitForNotification()` for server-initiated messages (diagnostics)
- Elvish LSP responds quickly to syntax errors (under 2 seconds)
- "method not found" LSP warnings are normal - Elvish LSP is minimal

## Gotchas

- Don't use `com.intellij.modules.ultimate` - restricts to Ultimate only!
- Must call `textDocument/didOpen` before any completion/hover requests
- Diagnostics are PUSHED via notification, not request/response
- If LSP isn't starting, check elvish binary path in settings first
