# Roadmap

Long-term vision for the Elvish JetBrains plugin.

## Current Release (v1.0.0) ✅

Core functionality complete:

- File type recognition for `.elv` files with custom icon
- Syntax highlighting via TextMate grammar
- LSP integration (completion, diagnostics, hover, go-to-definition)
- Syntax error highlighting via `elvish -compileonly`
- Run configurations with gutter icons
- 15 live templates + 2 file templates
- Editor features (folding, structure view, breadcrumbs)
- TODO/FIXME highlighting
- Spell checking in comments/strings
- Commenter and brace matching

## Immediate Next Steps

- [ ] Submit to JetBrains Marketplace
- [ ] Create GitHub releases with changelog
- [ ] Submit PR to [awesome-elvish](https://github.com/elves/awesome-elvish)

---

## v1.1.0 - Editor Polish

Low-effort, high-value improvements:

| Feature | Description | Effort |
|---------|-------------|--------|
| More Live Templates | `nop`, `range`, `all`, `one`, `slurp` | Low |
| Surround With | Wrap selection in `{ }`, `try { }`, `?(...)` | Low |
| Smart Enter | Auto-continue pipelines with `\|` | Medium |
| Quote Handling | Smart quote insertion and navigation | Medium |

## v1.2.0 - Elvish Navigation

Features to navigate Elvish projects:

| Feature | Description | Effort |
|---------|-------------|--------|
| Module Browser | Browse built-in modules (`str:`, `path:`, `math:`) with docs | Medium |
| rc.elv Quick Access | Action to open `~/.config/elvish/rc.elv` | Low |
| lib/*.elv Navigation | Navigate to user modules in `~/.config/elvish/lib/` | Medium |
| Module Completion | Enhanced completion for module functions | Medium |

## v1.3.0 - Run Enhancements

| Feature | Description | Effort |
|---------|-------------|--------|
| Run with Arguments | Prompt for script arguments before running | Low |
| Environment Profiles | Save/load environment variable sets | Medium |
| Output Filtering | Filter/search run output | Low |

## v2.0.0 - Developer Tools

| Feature | Description | Effort |
|---------|-------------|--------|
| REPL Integration | Interactive Elvish shell in IDE tool window | High |
| Test Runner | Run tests with results UI (supports [elvish-tap](https://github.com/tesujimath/elvish-tap) and [velvet](https://github.com/giancosta86/velvet)) | High |
| Snippet Manager | Create/share custom live templates | Medium |

## v2.1.0 - Advanced Features

| Feature | Description | Effort |
|---------|-------------|--------|
| Multi-file Refactoring | Rename across project (requires LSP support) | High |
| Call Hierarchy | Show function callers/callees | High |

---

## Blocked by Elvish LSP

These features require upstream changes to `elvish -lsp`:

| Feature | Required LSP Method |
|---------|---------------------|
| Find Usages | `textDocument/references` |
| Rename Symbol | `textDocument/rename` |
| Signature Help | `textDocument/signatureHelp` |
| Semantic Tokens | `textDocument/semanticTokens` |
| Inlay Hints | `textDocument/inlayHint` |

> Contributions to [elves/elvish](https://github.com/elves/elvish) could enable these features.

---

## Out of Scope

These are **shell/terminal features**, not IDE features. They're documented in [ECOSYSTEM.md](ECOSYSTEM.md) for users who want them:

- **Prompt themes** (Starship, oh-my-posh) - Terminal customization
- **Shell completions** (carapace-bin) - Terminal tab completion, not IDE
- **Directory tools** (zoxide, direlv) - Terminal navigation
- **Environment managers** (virtualenv, nvm, conda) - Terminal environments

The plugin focuses on **editing, running, and testing** Elvish code within the IDE.

---

## Contributing

Want to help? Pick an item from the roadmap and:
1. Check if there's an existing issue
2. Create a feature branch from `main`
3. Follow the [Contributing Guide](CONTRIBUTING.md)
4. Submit a PR

## Feedback

Have ideas? Open an issue on [GitHub](https://github.com/LorenzoWynberg/jetbrains-elvish/issues).
