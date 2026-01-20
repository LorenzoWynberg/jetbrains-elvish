# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Workflow (Ralph)

This project uses **Ralph** - an autonomous development loop that iterates through stories until complete.

### Running Ralph

```bash
# Start or continue development
./scripts/ralph/ralph.elv

# Resume current story
./scripts/ralph/ralph.elv --resume

# Reset state and start fresh
./scripts/ralph/ralph.elv --reset

# See all options
./scripts/ralph/ralph.elv --help
```

### Ralph Files

- `scripts/ralph/prd.json` - Stories with acceptance criteria and dependencies
- `scripts/ralph/prompt.md` - Agent instructions template
- `scripts/ralph/progress.txt` - Development log and learnings
- `scripts/ralph/state.json` - Current story tracking

### Manual Workflow

If not using Ralph, follow this loop:

1. **Plan**: Create detailed plan in `docs/plans/`
2. **Implement**: Work through plan step by step
3. **Test**: Run `./gradlew build` (includes tests)
4. **Self-Review**: Do 3 iterations asking:
   - Is everything complete for this task?
   - Is anything missing or broken?
   - Are there edge cases not handled?
5. **Commit**: Atomic commit per plan item, push immediately
6. **Iterate**: Repeat until complete

### Branching Strategy

- **dev**: Integration branch, PRs merge here
- **main**: Stable releases only
- **feat/story-X.Y.Z**: Feature branches for each story

### Commit Guidelines

- One commit per logical change
- Push after each commit
- Do NOT include "Co-Authored-By" lines
- Keep messages concise: `feat: STORY-010 - Create TextMate grammar`
- PRs always target `dev` branch

## Project Overview

JetBrains plugin providing Elvish shell language support. Integrates with Elvish's built-in LSP server (`elvish -lsp`) for completion, diagnostics, hover, and go-to-definition.

## Build Commands

```bash
./gradlew build      # Build (includes tests)
./gradlew runIde     # Run sandbox IDE with plugin
./gradlew buildPlugin # Create distributable ZIP
./gradlew clean      # Clean build artifacts
```

## Development Requirements

- **Java 21** (required by Gradle toolchain)
- **Gradle 8.11+** (wrapper uses 8.13)
- **Elvish shell** in PATH (for LSP features in runIde)

## Architecture

### Plugin Components

1. **Language Registration** (`ElvishLanguage.kt`, `ElvishFileType.kt`, `ElvishFile.kt`)
   - Registers `.elv` extension with IDE
   - PSI file wrapper for IntelliJ platform

2. **LSP Integration** (`lsp/` package)
   - `ElvishLspServerSupportProvider`: Triggers LSP on `.elv` file open
   - `ElvishLspServerDescriptor`: Configures `elvish -lsp` command
   - Uses JetBrains official LSP API

3. **Plugin Manifest** (`src/main/resources/META-INF/plugin.xml`)
   - Dependencies: `platform` and `ultimate` modules
   - Extensions: file type, LSP server support

### Key Design Decisions

- **No custom lexer/parser**: LSP handles language intelligence
- **Project-wide LSP**: Single server per project
- **Requires Elvish in PATH**: Configurable in Phase 6

## Current Status

**Completed (Phases 1-3):**
- STORY-001 to STORY-009: Project setup, file type, LSP integration

**Next (Phase 4):**
- STORY-010 to STORY-013: TextMate syntax highlighting

**Future (Phases 5-6):**
- STORY-014 to STORY-018: Parser definition (optional)
- STORY-019 to STORY-022: Settings & configuration
