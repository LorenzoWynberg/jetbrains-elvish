# Roadmap

Future plans for the Elvish JetBrains plugin.

## Current Release (v1.0.0)

- File type recognition for `.elv` files with custom icon
- Syntax highlighting via TextMate grammar
- LSP integration (completion, diagnostics, hover, go-to-definition)
- Run configurations with gutter icons
- 15 live templates + 2 file templates
- Editor features (folding, structure view, breadcrumbs)
- TODO/FIXME highlighting
- Spell checking in comments/strings
- Commenter and brace matching

## Next Steps

- [ ] Submit PR to [awesome-elvish](https://github.com/elves/awesome-elvish) to:
  - Replace abandoned PyCharm plugin entry with our JetBrains plugin
  - Add "JetBrains IDEs" to editor support section
- [ ] Submit to JetBrains Marketplace

## v1.1.0 - Editor Enhancements

Features we can implement without upstream LSP changes:

| Feature | Description | Status |
|---------|-------------|--------|
| More Live Templates | `nop`, `range`, `all`, `one`, `slurp` | Planned |
| Surround With | Wrap selection in `{ }`, `try { }`, `?(...)` | Planned |
| Smart Enter | Auto-continue pipelines | Planned |
| Carapace Integration | Leverage [carapace-bin](https://github.com/rsteube/carapace-bin) for 400+ command completions | Planned |

## v1.2.0 - Elvish Ecosystem

| Feature | Description | Status |
|---------|-------------|--------|
| Module Browser | Browse `str:`, `path:`, `math:`, `re:`, `file:`, `os:` modules | Planned |
| rc.elv Support | Special support for `~/.config/elvish/rc.elv` | Planned |
| lib/*.elv Support | Navigate to user modules in `~/.config/elvish/lib/` | Planned |
| epm Integration | Install packages via Elvish Package Manager | Planned |

## v2.0.0 - Advanced Features

| Feature | Description | Status |
|---------|-------------|--------|
| REPL Integration | Interactive Elvish shell in IDE terminal | Planned |
| zoxide Integration | Quick directory navigation via [zoxide](https://github.com/ajeetdsouza/zoxide) | Planned |
| Test Runner | Run [elvish-tap](https://github.com/tesujimath/elvish-tap) tests from IDE | Planned |

## Depends on Elvish LSP

These features require upstream changes to Elvish's built-in LSP (`elvish -lsp`):

| Feature | Required LSP Method | Status |
|---------|---------------------|--------|
| Find Usages | `textDocument/references` | Blocked |
| Rename Refactoring | `textDocument/rename` | Blocked |
| Signature Help | `textDocument/signatureHelp` | Blocked |
| Semantic Highlighting | `textDocument/semanticTokens` | Blocked |
| Inlay Hints | `textDocument/inlayHint` | Blocked |

> **Note:** Elvish's LSP is minimal by design. These features would need contributions to [elves/elvish](https://github.com/elves/elvish).

## Future Ideas

| Feature | Description |
|---------|-------------|
| Tilde Expansion Preview | Show what `~/` and `~user/` resolve to |
| Path Completion | Autocomplete file paths in strings |
| Env Var Hover | Show `$E:VAR` values on hover |
| Wildcard Preview | Show what `*`, `**`, `?` would match |
| Output Capture Hints | Explain `(cmd)` vs `?(cmd)` difference |

## Ecosystem

See [ECOSYSTEM.md](ECOSYSTEM.md) for companion tools (completions, prompts, modules).

## Contributing

Want to help? Pick an item from the roadmap and:
1. Check if there's an existing issue
2. Create a feature branch from `dev`
3. Follow the [Contributing Guide](CONTRIBUTING.md)
4. Submit a PR

## Feedback

Have ideas? Open an issue on [GitHub](https://github.com/LorenzoWynberg/intellij-elvish/issues).
