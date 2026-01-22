# Elvish Language Support for JetBrains IDEs

Syntax highlighting and language intelligence for [Elvish shell](https://elv.sh) files (`.elv`) in JetBrains IDEs.

## Supported IDEs

Works with **all JetBrains IDEs** version 2024.3 or later:

- IntelliJ IDEA (Community & Ultimate)
- PyCharm (Community & Professional)
- WebStorm
- GoLand
- RustRover
- CLion
- PhpStorm
- RubyMine
- Rider
- DataGrip
- DataSpell
- Fleet
- Android Studio (2024.3+)

**LSP features are free for all users** - no Ultimate/Professional license required.

## Features

### Language Support
- File type recognition for `.elv` files with custom icon
- Syntax highlighting via TextMate grammar
- Code completion, diagnostics, hover docs via Elvish's built-in LSP
- Go-to-definition navigation

### Editor Features
- **Line Comments**: Toggle comments with `Ctrl+/` (`Cmd+/` on Mac)
- **Brace Matching**: Highlight matching `{}`, `[]`, and `()`
- **Code Folding**: Collapse function bodies, control flow blocks, and multi-line lists/maps
- **Structure View**: Navigate functions, variables, and imports in the Structure tool window
- **Breadcrumbs**: See current code context (function name, block type) at top of editor
- **TODO Highlighting**: `TODO`, `FIXME`, `XXX`, `HACK`, `BUG` in comments appear in TODO tool window
- **Spell Checking**: Spell check comments and string literals

### Run Configuration
- Run `.elv` scripts directly from the IDE
- Configure script arguments, working directory, and environment variables
- Green play icon in gutter for quick script execution
- Right-click context menu: "Run &lt;filename&gt;"

### File Templates
Create new files via **New > Elvish Script** or **New > Elvish Module**:
- **Elvish Script**: Includes shebang and comment header
- **Elvish Module**: Includes example function for reusable modules

### Live Templates
Type abbreviation + `Tab` to expand code snippets:

| Abbreviation | Expands To | Description |
|--------------|------------|-------------|
| `fn` | `fn name {\|params\| ... }` | Function definition |
| `if` | `if condition { ... }` | If conditional |
| `ife` | `if condition { ... } else { ... }` | If-else |
| `for` | `for item $items { ... }` | For loop |
| `while` | `while condition { ... }` | While loop |
| `try` | `try { ... } catch e { ... }` | Try-catch |
| `tryf` | `try { ... } finally { ... }` | Try-finally |
| `use` | `use module` | Import module |
| `var` | `var name = value` | Variable declaration |
| `set` | `set name = value` | Variable assignment |
| `each` | `each {\|item\| ... }` | Iteration callback |
| `peach` | `peach {\|item\| ... }` | Parallel iteration |
| `lambda` | `{\|params\| ... }` | Anonymous function |
| `map` | `[&key=value]` | Map literal |
| `list` | `[items]` | List literal |

## Requirements

- JetBrains IDE 2024.3 or later (any edition)
- [Elvish](https://elv.sh) installed and available in PATH (or configured in Settings)

## Installation

### From Disk (Development)

1. Build the plugin: `./gradlew buildPlugin`
2. In your IDE: **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select `build/distributions/jetbrains-elvish-*.zip`

### From Marketplace (Coming Soon)

The plugin will be available on the JetBrains Marketplace.

## Usage

1. Open any `.elv` file
2. The LSP server starts automatically
3. Enjoy completions, diagnostics, and hover docs

## Documentation

- [Development Guide](docs/DEVELOPMENT.md) - Build, test, architecture
- [Contributing Guide](docs/CONTRIBUTING.md) - How to contribute
- [Roadmap](docs/ROADMAP.md) - Future plans
- [Ecosystem](docs/ECOSYSTEM.md) - Companion tools (completions, prompts, modules)
- [Ralph Guide](docs/ralph/RALPH.md) - Autonomous development loop

## Resources

- [Elvish Shell](https://elv.sh)
- [Elvish Language Reference](https://elv.sh/ref/language.html)
- [Elvish Command Reference](https://elv.sh/ref/command.html)

## License

MIT
<!-- RALPH_STREAMING_TEST -->
