# Marketing & Launch Plan

## Feature Ideas for Future Releases

### High Value (LSP-powered)
| Feature | Description | Effort |
|---------|-------------|--------|
| **Find Usages** | Find all references to a variable/function | Low - LSP supports it |
| **Rename Refactoring** | Rename symbol across files | Low - LSP supports it |
| **Semantic Highlighting** | Richer colors using LSP semantic tokens | Medium |
| **Inlay Hints** | Show parameter names inline | Medium |
| **Signature Help** | Show function signatures while typing | Low - LSP supports it |

### Editor Enhancements
| Feature | Description | Effort |
|---------|-------------|--------|
| **More Live Templates** | `nop`, `range`, `all`, `one`, `slurp` | Low |
| **Surround With** | Wrap selection in `{ }`, `try { }`, `?(...)` | Low |
| **Smart Enter** | Auto-continue pipelines with `\|` | Medium |
| **Wildcard Preview** | Show what `*`, `**`, `?` would match | Medium |

### Advanced Features
| Feature | Description | Effort |
|---------|-------------|--------|
| **REPL Integration** | Interactive Elvish shell in IDE terminal | High |
| **Module Browser** | Browse str:, path:, math:, re:, file:, os: modules | Medium |
| **epm Integration** | Install packages via Elvish Package Manager | Medium |
| **rc.elv Support** | Special support for `~/.config/elvish/rc.elv` | Low |
| **lib/*.elv Support** | Navigate to user modules in `~/.config/elvish/lib/` | Low |

### Nice to Have
| Feature | Description | Effort |
|---------|-------------|--------|
| **Tilde Expansion Preview** | Show what `~/` and `~user/` resolve to | Low |
| **Path Completion** | Autocomplete file paths in strings | Medium |
| **Env Var Hover** | Show `$E:VAR` values on hover | Medium |
| **Store Browser** | Browse persistent store (`store:` module) | Medium |
| **Output Capture Hints** | Explain `(cmd)` vs `?(cmd)` difference | Low |

---

## Marketplace Listing

### Plugin Name
**Elvish Language Support**

### Tagline
> Full-featured Elvish shell support with LSP-powered intelligence

### Short Description (400 chars)
```
Syntax highlighting, code completion, diagnostics, and navigation for Elvish shell scripts.
Powered by Elvish's built-in LSP server for accurate, real-time intelligence.
Includes run configurations, live templates, code folding, and structure view.
Works with all JetBrains IDEs 2024.3+.
```

### Full Description
```markdown
# Elvish Language Support

Comprehensive support for the [Elvish shell](https://elv.sh) in JetBrains IDEs.

## Features

### Intelligent Editing
- **Code Completion** - Variables, functions, modules, and builtins
- **Real-time Diagnostics** - Syntax errors as you type
- **Hover Documentation** - Quick docs for any symbol
- **Go to Definition** - Jump to function/variable declarations

### Editor Features
- **Syntax Highlighting** - Full TextMate grammar support
- **Code Folding** - Collapse functions, lambdas, and blocks
- **Structure View** - Navigate functions and variables
- **Breadcrumbs** - See your location in nested code
- **Brace Matching** - Highlight matching brackets

### Productivity
- **Run Configurations** - Execute scripts with one click
- **Gutter Icons** - Run button next to fn definitions
- **Live Templates** - Quick snippets (fn, if, for, use)
- **File Templates** - New Elvish Script/Module wizards
- **Commenter** - Toggle comments with Cmd+/

## Requirements
- JetBrains IDE 2024.3 or later
- [Elvish](https://elv.sh) installed (for LSP features)

## Getting Started
1. Install Elvish: `brew install elvish` (macOS) or see [installation guide](https://elv.sh/get/)
2. Open any `.elv` file
3. The LSP server starts automatically

## Links
- [Elvish Shell](https://elv.sh)
- [GitHub Repository](https://github.com/LorenzoWynberg/intellij-elvish)
- [Report Issues](https://github.com/LorenzoWynberg/intellij-elvish/issues)
```

### Tags
`elvish`, `shell`, `scripting`, `lsp`, `syntax-highlighting`, `code-completion`

### Category
**Languages**

---

## Screenshots Needed

1. **Hero Shot** - Editor with syntax highlighting + completion popup
2. **Diagnostics** - Red squiggles with error tooltip
3. **Hover Docs** - Hover over builtin showing documentation
4. **Structure View** - Side panel with functions/variables
5. **Run Configuration** - Run dialog with Elvish config
6. **Go to Definition** - Ctrl+click navigation
7. **Live Templates** - Template expansion in action

### Screenshot Specs
- 1280x800 or 1600x1000 recommended
- Dark theme (Darcula) preferred
- Clean, focused content
- Highlight the feature with annotations if needed

---

## Launch Checklist

### Pre-Launch
- [ ] Build final plugin: `./gradlew buildPlugin`
- [ ] Test on IntelliJ IDEA Community
- [ ] Test on PyCharm Community
- [ ] Test on WebStorm (trial)
- [ ] Create JetBrains Hub account
- [ ] Prepare vendor info (name, email, URL)
- [ ] Take all screenshots
- [ ] Write changelog for v1.0.0

### Marketplace Submission
- [ ] Upload plugin ZIP
- [ ] Fill in all metadata
- [ ] Add screenshots
- [ ] Set pricing (Free)
- [ ] Submit for review

### Post-Launch
- [ ] Announce on Reddit r/elvish
- [ ] Post on Elvish Discord/Matrix
- [ ] Tweet/post about it
- [ ] Add badge to GitHub README
- [ ] Monitor reviews and respond

---

## Version Roadmap

### v1.0.0 (Launch)
- File type recognition
- Syntax highlighting
- LSP integration (completion, diagnostics, hover, go-to-def)
- Run configurations with gutter icons
- 15 live templates + 2 file templates
- Editor features (folding, structure, breadcrumbs)
- TODO/FIXME highlighting
- Spell checking in comments/strings
- Commenter and brace matching

### v1.1.0 (Fast Follow)
- Find usages
- Rename refactoring
- Signature help
- More live templates

### v1.2.0 (Polish)
- Semantic highlighting
- Inlay hints
- Surround with actions
- Smart enter handling

### v2.0.0 (Advanced)
- REPL integration
- Module browser
- rc.elv special support

---

## Metrics to Track

- Downloads per week
- Active users (via anonymous stats if enabled)
- GitHub stars
- Marketplace rating
- Issue volume

---

## Community Outreach

### Elvish Community
- [Elvish GitHub Discussions](https://github.com/elves/elvish/discussions)
- Elvish Matrix/IRC channels
- r/elvish subreddit

### JetBrains Community
- JetBrains Plugin Developers Slack
- IntelliJ Plugin Development forum

### Content Ideas
- Blog post: "Building an LSP-powered JetBrains Plugin"
- Tutorial: "Getting Started with Elvish in IntelliJ"
- Video: Quick demo of features
