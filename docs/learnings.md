# Learnings

Consolidated learnings from development. Updated by Ralph after each story.

---

## Elvish Language

### Syntax
- Uses `catch e` syntax where `e` is the error variable (not `catch (e)`)
- For loops use `for var container` syntax (no `in` keyword)
- Allows hyphens in identifiers (e.g., `my-function`)
- Lambda expressions use `{|params| body}` syntax
- Function parameters extracted from `{|param1 param2|}` syntax

### Features
- Rational numbers (e.g., 22/7) are valid numeric literals
- `$@` explode syntax for list expansion
- String comparison operators use `*s` variants (e.g., `<s`, `>s`)
- Modules don't need shebang (imported via `use`, not executed directly)
- Variables inside strings handled via interpolation

### Operators
- Range operator: `1..10` (must distinguish from float `1.5`)
- Lexer must check for digit after `.` to distinguish float from range
- `>>` redirect operator is context-dependent

---

## IntelliJ Plugin Development

### Project Setup
- Gradle with IntelliJ Platform Plugin 2.10.5
- Kotlin 1.9.25 with Java 21 toolchain
- Requires Java 17+ to build (IntelliJ JBR works well)
- `instrumentationTools()` needed for proper plugin instrumentation

### Language Support
- `Language` class is fundamental - use singleton pattern for consistent reference
- `LanguageFileType` associates file extensions with the language
- `PsiFileBase` is the standard base for PSI files
- `FileViewProvider` bridges virtual files and PSI

### LSP Integration
- `com.intellij.modules.lsp` is the correct module for cross-IDE LSP support
- `com.intellij.modules.ultimate` restricts to IntelliJ IDEA Ultimate only
- `LspServerSupportProvider` is the entry point for LSP integration
- `ProjectWideLspServerDescriptor` for single server per project
- `GeneralCommandLine` configures process launch
- `serverStarter.ensureServerStarted` handles lifecycle
- LSP API is experimental - warnings are expected
- As of 2025.3, JetBrains unified distribution makes LSP free for all users

### Parser & Lexer
- `IElementType` requires a Language instance to associate tokens
- `TokenSet.create()` groups related token types for parser use
- `@JvmField` annotation exposes Kotlin vals as Java fields
- `LexerBase` provides clean interface for custom lexers
- `PsiBuilder` marker pattern: `mark()`, `done(type)`, `drop()`, `precede()`
- `ParserDefinition` ties together lexer, parser, and PSI file creation
- `createElement()` is optional for minimal parsers
- Condition parsing must stop at LBRACE to avoid consuming the block
- Block parsing must skip whitespace before checking for RBRACE

### TextMate Integration
- TextMate extensions use namespace: `org.jetbrains.plugins.textmate`
- Extension point is `bundleProvider` (not `textmate.bundleProvider`)
- `TextMateBundleProvider` interface is in `org.jetbrains.plugins.textmate.api`
- `PluginBundle` takes name (String) and path (Path) to grammar directory
- Grammar uses `scopeName` for language identification (e.g., `source.elvish`)
- Oniguruma regex supports lookbehind `(?<![...])`
- Standard scope names map to theme colors (grammar assigns scopes, themes map colors)

### Editor Features
- `Commenter` interface: 5 methods for line/block comments
- `PairedBraceMatcher`: `BracePair(left, right, structural)` - structural flag for folding
- `FoldingBuilderEx` provides more control than `FoldingBuilder`
- `DumbAware` interface allows features to work during indexing
- `BreadcrumbsProvider`: `getLanguages()`, `acceptElement()`, `getElementInfo()`, `getElementTooltip()`
- Framework handles nested breadcrumbs by traversing parent elements

### Structure View
- `PsiStructureViewFactory` creates `TreeBasedStructureViewBuilder` instances
- `StructureViewModelBase` provides base with sorting support
- `StructureViewTreeElement` wraps PSI elements for tree display
- `SortableTreeElement` interface enables alpha sorting
- `AllIcons.Nodes.*` provides standard icons (Function, Variable, Include)

### Run Configurations
- `RunConfigurationBase<Options>` separates logic from state persistence
- `StoredProperty` with `provideDelegate` provides type-safe storage
- `SimpleConfigurationType` handles factory, icon, and registration boilerplate
- `CommandLineState` provides standard run infrastructure
- `KillableColoredProcessHandler` for colored output and stop button support
- `ProcessTerminatedListener` displays exit code when process finishes
- `LazyRunConfigurationProducer` creates factory lazily for performance
- `RunLineMarkerContributor.Info` with `java.util.function.Function` (not deprecated `com.intellij.util.Function`)
- `ExecutorAction.getActions(0)` provides standard run actions
- Show gutter marker only at first leaf element to avoid duplicates

### Settings & Configuration
- `@State` annotation's `storages` parameter specifies XML file name
- `@Service(Service.Level.PROJECT)` for project-scoped services
- `PersistentStateComponent` requires mutable properties for XML serialization
- `Project.getService()` retrieves service instance
- Kotlin UI DSL: `panel { row { ... } }` builder pattern
- `bindText()` connects UI component to property reference
- `projectConfigurable` uses `parentId="language"` for Languages & Frameworks
- Settings are read each time, so changes take effect on restart

### File Templates
- Templates use `.ft` suffix in `fileTemplates/` directory
- `CreateFileFromTemplateAction` for template-based file creation
- `internalFileTemplate` extension makes templates available
- Action must be added to `NewGroup` to appear in "New" menu
- Template variables: `${VARIABLE_NAME}` syntax
- `addKind()` in `buildDialog()` for multiple template options

### Live Templates
- `liveTemplateContext` extension requires `contextId` matching XML context name
- Template variables use `alwaysStopAt="true"` for Tab navigation
- `$END$` is special variable for final cursor position
- Use `&#10;` for newlines in XML value attribute
- Default values help users understand expected input format

### Notifications
- Notification groups registered in plugin.xml via `notificationGroup` extension
- `NotificationGroupManager.getInstance().getNotificationGroup()` retrieves groups
- `ConcurrentHashMap.putIfAbsent()` for "show once" logic
- `Project.locationHash` provides stable project identifier

### Icons & Marketplace
- JetBrains Marketplace requires 40x40 SVG icons with 2px padding
- `pluginIcon.svg` must be in META-INF folder (not /icons)
- SVG icons scale well in modern IDEs
- `IconLoader.getIcon` uses classpath-relative paths
- Plugin is "dynamic" - can be enabled/disabled without IDE restart

### Logging & Debugging
- `Logger.getInstance()` with class reference is standard pattern
- `LOG.info()` writes to idea.log (Help > Show Log)
- `commandLineString` provides full command with arguments for diagnostics

---

## Testing

### LSP Testing
- `ProcessBuilder` can start and interact with `elvish -lsp`
- LSP server responds to JSON-RPC initialize request
- Use `textDocument/didOpen` before requesting completion/hover
- Completion: `textDocument/completion` with position
- Hover: `textDocument/hover` with position
- Diagnostics: pushed via `textDocument/publishDiagnostics` notification
- Use `waitForNotification()` for server-initiated messages
- Document changes trigger diagnostics via `textDocument/didChange`
- Elvish LSP responds quickly to syntax errors (under 2 seconds)

### General Testing
- Test `PsiBuilder` implementation requires many interface method stubs
- Shared test utilities reduce code duplication
- Manual GUI verification can be replaced with automated integration tests

---

## Build & Environment

### Gradle
- `untilBuild.set(provider { null })` removes upper version bound
- Plugin verifier: `intellijPlatform.pluginVerification.ides { recommended() }`
- `bundledPlugin()` in intellijPlatform dependencies for bundled plugins
- Gradle wrapper ensures consistent builds across environments
- Environment variables for signing: CERTIFICATE_CHAIN, PRIVATE_KEY, etc.

### macOS Specifics
- Use `stat -f%z` instead of `wc -c` for file size (more reliable in pipes)
- JAVA_HOME must be set if default Java is lower than 17

---

## Gotchas & Debugging

### Common Mistakes
- Don't use `com.intellij.modules.ultimate` - restricts to Ultimate only, use `com.intellij.modules.lsp` instead
- Don't use `wc -c` in Elvish pipes - can fail silently, use `stat -f%z` on macOS
- Don't forget to check for digit after `.` in lexer - otherwise `1..10` looks like float `1.` followed by `.10`

### Debugging Tips
- Check idea.log via Help > Show Log for plugin errors
- LSP "method not found" warnings are normal - Elvish LSP is minimal
- If LSP isn't starting, verify elvish binary path in settings

### Things That Look Right But Aren't
- `com.intellij.util.Function` is deprecated - use `java.util.function.Function`
- TextMate namespace is `org.jetbrains.plugins.textmate` not `com.intellij`

---

## Code Organization

### Package Structure
- `parser/` - TokenTypes, ElementTypes, Lexer, Parser, ParserDefinition
- `lsp/` - LSP server support and descriptors
- `settings/` - Persistent settings and configurable UI
- `editor/` - Commenter, brace matcher, folding, breadcrumbs
- `editor/structure/` - Structure view components
- `run/` - Run configurations, execution, gutter icons
- `actions/` - File creation actions
- `templates/` - Live template context types
- `textmate/` - TextMate bundle provider

### Best Practices
- Singleton pattern for Language and FileType
- Separate config logic from state persistence
- Editor features in dedicated `editor` package
- Use companion object constants for default values
- Clear caches on settings change to allow re-checking
