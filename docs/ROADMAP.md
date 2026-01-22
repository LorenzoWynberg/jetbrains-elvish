# Roadmap

Long-term vision for the Elvish JetBrains plugin.

## Current Release (v1.0.0) ✅

Core functionality complete:

- File type recognition for `.elv` files with custom icon
- Syntax highlighting via TextMate grammar
- LSP integration (completion, diagnostics, hover, go-to-definition)
- Run configurations with gutter icons
- 15 live templates + 2 file templates
- Editor features (folding, structure view, breadcrumbs)
- TODO/FIXME highlighting
- Spell checking in comments/strings
- Commenter and brace matching

## Immediate Next Steps

- [ ] Submit PR to [awesome-elvish](https://github.com/elves/awesome-elvish):
  - Replace abandoned PyCharm plugin with our JetBrains plugin
  - Add "JetBrains IDEs" to editor support section
- [ ] Submit to JetBrains Marketplace
- [ ] Create GitHub releases with changelog

---

## v1.1.0 - Quick Wins

Low-effort, high-value improvements:

| Feature | Description | Priority |
|---------|-------------|----------|
| More Live Templates | `nop`, `range`, `all`, `one`, `slurp` | High |
| Surround With | Wrap selection in `{ }`, `try { }`, `?(...)` | High |
| Smart Enter | Auto-continue pipelines with `\|` | Medium |
| Quote Handling | Smart quote insertion and navigation | Medium |

## v1.2.0 - Elvish Ecosystem

Features that make Elvish development easier:

| Feature | Description | Priority |
|---------|-------------|----------|
| Module Browser | Browse built-in modules (`str:`, `path:`, `math:`, `re:`, `file:`, `os:`) with docs | High |
| rc.elv Support | Quick navigation to `~/.config/elvish/rc.elv` | High |
| lib/*.elv Support | Navigate to user modules in `~/.config/elvish/lib/` | High |
| epm Browser | View installed packages, install new ones | Medium |
| Module Completion | Enhanced completion for module functions | Medium |

## v1.3.0 - Developer Experience

| Feature | Description | Priority |
|---------|-------------|----------|
| Carapace Docs | Document [carapace-bin](https://github.com/rsteube/carapace-bin) integration for 400+ command completions | High |
| Run with Args | Prompt for script arguments before running | Medium |
| Environment Profiles | Save/load environment variable sets | Medium |
| Script Debugging | Basic print-based debugging support | Low |

## v2.0.0 - Advanced Features

| Feature | Description | Priority |
|---------|-------------|----------|
| REPL Integration | Interactive Elvish shell in IDE tool window | High |
| Test Runner | Run [elvish-tap](https://github.com/tesujimath/elvish-tap) tests with results UI | Medium |
| zoxide Integration | Quick directory navigation via [zoxide](https://github.com/ajeetdsouza/zoxide) | Medium |
| Snippet Manager | Create/share custom live templates | Low |

## v2.1.0 - Power User Features

| Feature | Description | Priority |
|---------|-------------|----------|
| Multi-file Refactoring | Rename across project (if LSP supports) | Medium |
| Call Hierarchy | Show function callers/callees | Medium |
| Type Hints | Show inferred types inline | Low |

---

## Blocked by Elvish LSP

These features require upstream changes to `elvish -lsp`:

| Feature | Required LSP Method | Tracking |
|---------|---------------------|----------|
| Find Usages | `textDocument/references` | Not implemented |
| Rename Symbol | `textDocument/rename` | Not implemented |
| Signature Help | `textDocument/signatureHelp` | Not implemented |
| Semantic Tokens | `textDocument/semanticTokens` | Not implemented |
| Inlay Hints | `textDocument/inlayHint` | Not implemented |

> Contributions to [elves/elvish](https://github.com/elves/elvish) could enable these features.

---

## Future Ideas

Exploring for later versions:

| Feature | Description |
|---------|-------------|
| Tilde Expansion Preview | Show what `~/` and `~user/` resolve to |
| Path Completion | Autocomplete file paths in strings |
| Env Var Hover | Show `$E:VAR` values on hover |
| Wildcard Preview | Show what `*`, `**`, `?` would match |
| Output Capture Hints | Explain `(cmd)` vs `?(cmd)` difference |
| Nix Integration | Support for [elvish.nix](https://github.com/tesujimath/elvish.nix) |
| direnv/direlv | Directory-specific environments |

---

## Ecosystem

See [ECOSYSTEM.md](ECOSYSTEM.md) for companion tools users can install alongside the plugin.

## Contributing

Want to help? Pick an item from the roadmap and:
1. Check if there's an existing issue
2. Create a feature branch from `dev`
3. Follow the [Contributing Guide](CONTRIBUTING.md)
4. Submit a PR

## Feedback

Have ideas? Open an issue on [GitHub](https://github.com/LorenzoWynberg/intellij-elvish/issues).
