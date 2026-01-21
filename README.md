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

- File type recognition for `.elv` files with custom icon
- Syntax highlighting via TextMate grammar
- Code completion, diagnostics, hover docs via Elvish's built-in LSP
- Go-to-definition and find references

## Requirements

- JetBrains IDE 2024.3 or later (any edition)
- [Elvish](https://elv.sh) installed and available in PATH (or configured in Settings)

## Installation

### From Disk (Development)

1. Build the plugin: `./gradlew buildPlugin`
2. In your IDE: **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select `build/distributions/intellij-elvish-*.zip`

### From Marketplace (Coming Soon)

The plugin will be available on the JetBrains Marketplace.

## Usage

1. Open any `.elv` file
2. The LSP server starts automatically
3. Enjoy completions, diagnostics, and hover docs

## Documentation

- [Development Guide](docs/DEVELOPMENT.md) - Build, test, architecture
- [Contributing Guide](docs/CONTRIBUTING.md) - How to contribute
- [Ralph Guide](docs/RALPH.md) - Autonomous development loop

## Resources

- [Elvish Shell](https://elv.sh)
- [Elvish Language Reference](https://elv.sh/ref/language.html)
- [Elvish Command Reference](https://elv.sh/ref/command.html)

## License

MIT
