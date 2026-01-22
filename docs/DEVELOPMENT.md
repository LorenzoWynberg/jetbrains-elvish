# Development Guide

## Requirements

- **Java 21** - Required by Gradle toolchain
- **Gradle 8.11+** - Wrapper uses 8.13
- **Elvish shell** - In PATH for LSP features when testing

## Build Commands

```bash
./gradlew build        # Build and run tests
./gradlew runIde       # Run sandbox IDE with plugin
./gradlew buildPlugin  # Create distributable ZIP
./gradlew clean        # Clean build artifacts
```

## Project Structure

```
src/main/
├── kotlin/com/elvish/plugin/
│   ├── ElvishLanguage.kt      # Language registration
│   ├── ElvishFileType.kt      # File type for .elv
│   ├── ElvishFile.kt          # PSI file wrapper
│   ├── ElvishIcons.kt         # Icon definitions
│   ├── lsp/
│   │   ├── ElvishLspServerSupportProvider.kt
│   │   ├── ElvishLspServerDescriptor.kt
│   │   ├── ElvishBinaryChecker.kt
│   │   └── ElvishNotifications.kt
│   ├── parser/
│   │   ├── ElvishTokenTypes.kt       # Lexer token types
│   │   ├── ElvishElementTypes.kt     # Parser AST node types
│   │   ├── ElvishLexer.kt            # Tokenizer
│   │   ├── ElvishParser.kt           # AST builder
│   │   └── ElvishParserDefinition.kt # Parser factory
│   ├── editor/
│   │   ├── ElvishCommenter.kt        # Line comment support (Ctrl+/)
│   │   ├── ElvishBraceMatcher.kt     # Matching braces highlighting
│   │   ├── ElvishFoldingBuilder.kt   # Code folding for blocks
│   │   ├── ElvishBreadcrumbsProvider.kt  # Breadcrumbs navigation
│   │   ├── ElvishIndexPatternBuilder.kt  # TODO/FIXME highlighting in comments
│   │   ├── ElvishSpellcheckingStrategy.kt  # Spell checking for comments and strings
│   │   └── structure/
│   │       ├── ElvishStructureViewFactory.kt  # Structure view builder factory
│   │       ├── ElvishStructureViewModel.kt    # Structure view model
│   │       └── ElvishStructureViewElement.kt  # Tree element for structure view
│   ├── settings/
│   │   ├── ElvishSettings.kt         # Plugin settings storage
│   │   └── ElvishConfigurable.kt     # Settings UI panel
│   ├── run/
│   │   ├── ElvishConfigurationType.kt     # Run configuration type
│   │   ├── ElvishRunConfiguration.kt      # Run configuration with validation
│   │   ├── ElvishRunConfigurationOptions.kt  # Configuration state persistence
│   │   ├── ElvishRunConfigurationEditor.kt   # Configuration UI
│   │   ├── ElvishRunConfigurationProducer.kt # Context menu run configuration
│   │   ├── ElvishRunLineMarkerProvider.kt # Gutter run icons for .elv files
│   │   └── ElvishRunProfileState.kt       # Script execution and output
│   ├── actions/
│   │   └── CreateElvishFileAction.kt      # New > Elvish Script file creation
│   └── textmate/
│       └── ElvishTextMateBundleProvider.kt
└── resources/
    ├── META-INF/plugin.xml    # Plugin manifest
    ├── icons/elvish.svg       # File icon
    ├── fileTemplates/
    │   ├── Elvish Script.elv.ft    # Elvish script file template
    │   └── Elvish Script.elv.html  # Template description
    └── textmate/
        └── elvish.tmLanguage.json
```

## Architecture

### Plugin Components

1. **Language Registration** (`ElvishLanguage.kt`, `ElvishFileType.kt`, `ElvishFile.kt`)
   - Registers `.elv` extension with the IDE
   - PSI file wrapper for IntelliJ platform

2. **LSP Integration** (`lsp/` package)
   - `ElvishLspServerSupportProvider`: Triggers LSP on `.elv` file open
   - `ElvishLspServerDescriptor`: Configures `elvish -lsp` command
   - `ElvishBinaryChecker`: Verifies elvish binary availability before starting LSP
   - `ElvishNotifications`: Shows user notifications for missing binary
   - Uses JetBrains official LSP API

3. **Syntax Highlighting** (`textmate/` package)
   - TextMate grammar for Elvish syntax
   - Material Palenight-inspired color scheme

4. **Parser** (`parser/` package)
   - `ElvishTokenTypes`: Token type definitions for lexer
   - `ElvishElementTypes`: AST node types for parser
   - `ElvishLexer`: Tokenizes Elvish source code
   - `ElvishParser`: Builds basic AST structure
   - `ElvishParserDefinition`: Factory that ties lexer, parser, and file creation together

5. **Settings** (`settings/` package)
   - `ElvishSettings`: Project-level settings stored in `elvish.xml`
   - `ElvishConfigurable`: Settings UI panel for configuring elvish path
   - Configurable elvish path (default: 'elvish' from PATH)

6. **Run Configuration** (`run/` package)
   - `ElvishConfigurationType`: Registers "Elvish" run configuration type in Run > Edit Configurations
   - `ElvishRunConfiguration`: Stores script path, arguments, working directory, environment variables
   - `ElvishRunConfigurationOptions`: Persistent state storage for configuration properties
   - `ElvishRunConfigurationEditor`: UI for editing configuration settings
   - `ElvishRunConfigurationProducer`: Creates run configurations from context (right-click menu)
   - `ElvishRunLineMarkerProvider`: Shows gutter run icons at line 1 of .elv files
   - `ElvishRunProfileState`: Executes scripts and displays output in Run tool window

7. **Editor Features** (`editor/` package)
   - `ElvishCommenter`: Line comment support using `# ` prefix (Ctrl+/ / Cmd+/)
   - `ElvishBraceMatcher`: Highlights matching braces `{}`, brackets `[]`, and parentheses `()`
   - `ElvishFoldingBuilder`: Code folding for function bodies, control flow blocks, and multi-line lists/maps
   - `ElvishBreadcrumbsProvider`: Breadcrumbs navigation showing current code context (function, control flow blocks)
   - `ElvishIndexPatternBuilder`: TODO/FIXME/XXX/HACK/BUG highlighting in # comments for TODO tool window
   - `ElvishSpellcheckingStrategy`: Spell checking in comments and string literals (not barewords)
   - `structure/` subpackage: Structure view support
     - `ElvishStructureViewFactory`: Creates structure view builders
     - `ElvishStructureViewModel`: Controls structure view display and sorting
     - `ElvishStructureViewElement`: Parses file to find functions (fn), variables (var), and imports (use)

8. **File Templates** (`actions/` package and `fileTemplates/` resources)
   - `CreateElvishFileAction`: Action for "New > Elvish Script" in context menu
   - `Elvish Script.elv.ft`: File template with shebang and comment header
   - `Elvish Script.elv.html`: Template description shown in New File dialog

9. **Plugin Manifest** (`META-INF/plugin.xml`)
   - Dependencies: `platform`, `textmate` modules, plus optional `lsp` module
   - The `lsp` module is available in all JetBrains IDEs since 2024.2 (free for all users)
   - Extensions: file type, LSP server support, TextMate bundle, parser definition, project settings, commenter, brace matcher, folding builder, structure view, breadcrumbs, run configuration type, run line marker, run configuration producer, internal file template, index pattern builder, spellchecker support
   - Actions: "New > Elvish Script" for file creation

### Key Design Decisions

- **Minimal parser**: Basic AST structure for IDE features; LSP handles language intelligence
- **Project-wide LSP**: Single server per project
- **TextMate for highlighting**: Simpler than custom lexer, good enough for syntax colors
- **Configurable elvish path**: Settings allow custom path; defaults to 'elvish' in PATH

## Testing

Run the sandbox IDE to test the plugin:

```bash
./gradlew runIde
```

This launches a fresh IDE instance with the plugin installed. Open any `.elv` file to test features.

## Debugging

1. Run `./gradlew runIde` with `--debug-jvm`
2. Attach remote debugger to port 5005
3. Set breakpoints in plugin code

## Resources

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [JetBrains LSP API](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html)
- [TextMate Bundles](https://plugins.jetbrains.com/docs/intellij/textmate.html)
