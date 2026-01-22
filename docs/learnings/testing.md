# Testing

Learnings about testing patterns and utilities.

## LSP Testing

- `ProcessBuilder` can start and interact with `elvish -lsp`
- LSP server responds to JSON-RPC initialize request
- Use `textDocument/didOpen` before requesting completion/hover
- Use `waitForNotification()` for server-initiated messages (diagnostics)
- Elvish LSP responds quickly to syntax errors (under 2 seconds)
- Shared test utilities reduce code duplication between test classes

## Test Infrastructure

- Test `PsiBuilder` implementation requires many interface method stubs
- Tests go in `src/test/kotlin/` mirroring main source structure
- JUnit 4.13.2 with IntelliJ test framework

## What to Test

- Unit tests for logic (lexer, parser, settings)
- Integration tests for features (LSP communication)
- Skip tests for: pure UI code, trivial getters/setters, external integration

## Gotchas

- Manual GUI verification can often be replaced with automated integration tests
- LSP tests must open document before making requests
- Tests that rely on elvish binary need it installed locally
- Build requires Java 17+ (uses JBR from IntelliJ IDEA)
