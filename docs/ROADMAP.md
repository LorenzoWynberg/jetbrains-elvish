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

## v1.1.0 - LSP Enhancements

| Feature | Description | Status |
|---------|-------------|--------|
| Find Usages | Find all references to a variable/function | Planned |
| Rename Refactoring | Rename symbol across files | Planned |
| Signature Help | Show function signatures while typing | Planned |
| More Live Templates | `nop`, `range`, `all`, `one`, `slurp` | Planned |

## v1.2.0 - Editor Polish

| Feature | Description | Status |
|---------|-------------|--------|
| Semantic Highlighting | Richer colors using LSP semantic tokens | Planned |
| Inlay Hints | Show parameter names inline | Planned |
| Surround With | Wrap selection in `{ }`, `try { }`, `?(...)` | Planned |
| Smart Enter | Auto-continue pipelines | Planned |

## v2.0.0 - Advanced Features

| Feature | Description | Status |
|---------|-------------|--------|
| REPL Integration | Interactive Elvish shell in IDE terminal | Planned |
| Module Browser | Browse `str:`, `path:`, `math:`, `re:`, `file:`, `os:` modules | Planned |
| epm Integration | Install packages via Elvish Package Manager | Planned |
| rc.elv Support | Special support for `~/.config/elvish/rc.elv` | Planned |
| lib/*.elv Support | Navigate to user modules in `~/.config/elvish/lib/` | Planned |

## Future Ideas

| Feature | Description |
|---------|-------------|
| Tilde Expansion Preview | Show what `~/` and `~user/` resolve to |
| Path Completion | Autocomplete file paths in strings |
| Env Var Hover | Show `$E:VAR` values on hover |
| Wildcard Preview | Show what `*`, `**`, `?` would match |
| Output Capture Hints | Explain `(cmd)` vs `?(cmd)` difference |

## Contributing

Want to help? Pick an item from the roadmap and:
1. Check if there's an existing issue
2. Create a feature branch from `dev`
3. Follow the [Contributing Guide](CONTRIBUTING.md)
4. Submit a PR

## Feedback

Have ideas? Open an issue on [GitHub](https://github.com/LorenzoWynberg/intellij-elvish/issues).
