# LSP Integration

> **TL;DR:** Use `com.intellij.modules.lsp` (not ultimate), `elvish -lsp` for server, stdio communication, diagnostics are pushed via notifications.

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

## IntelliJ LSP API Versions

- **2024.3**: Base LSP support via `platform.lsp.serverSupportProvider`
- **2025.2+**: Adds `LspCustomization` API for intercepting diagnostics, custom handlers
- `createLsp4jClient()` method doesn't exist in 2024.3 - can't override client
- `Lsp4jClient.publishDiagnostics()` is final - cannot intercept in 2024.3

## Diagnostics Display Issue (2024.3)

IntelliJ 2024.3 receives `publishDiagnostics` from Elvish LSP but may not display them:
- Debug logs show: `Received diagnostics notification for file`
- Elvish LSP doesn't advertise `diagnosticProvider` capability (returns empty)
- IntelliJ still receives diagnostics via notification but internal processing may filter them

**Workaround:** Use `elvish -compileonly -c <code>` via regular Annotator instead of LSP diagnostics:
- Runs elvish syntax checking directly
- Parses error output (line:col format)
- Creates annotations with `HighlightSeverity.ERROR`
- Shows errors in Problems panel and (with proper attributes) inline

## Annotator Best Practices

- **Simple annotation works**: Just `holder.newAnnotation(HighlightSeverity.ERROR, message).range(range).create()` is sufficient
- **Parse full error ranges**: Elvish provides end column (e.g., `1:6-15`), use it to highlight full tokens
- **Handle EOF errors**: For "should be '}'" at EOF, find the unclosed `{` and highlight that instead
- **Contextual messages**: Change "should be '}'" to "Unclosed '{' - missing '}'" when highlighting opening brace
- **Cache results**: Use `ConcurrentHashMap` with TTL to avoid running elvish on every keystroke

## Gotchas

- Don't use `com.intellij.modules.ultimate` - restricts to Ultimate only!
- Must call `textDocument/didOpen` before any completion/hover requests
- Diagnostics are PUSHED via notification, not request/response
- If LSP isn't starting, check elvish binary path in settings first
- **ExternalAnnotator didn't work for inline highlighting** - use regular Annotator instead
- `elvish -compileonly -` doesn't read from stdin - use `-c <code>` flag instead
- **Empty line errors need relocation** - highlight the source (unclosed brace) not the empty line
- **Annotation shows in Problems but not inline?** The range might be outside PSI elements or on empty line
