# IntelliJ Plugin Development

Core patterns for IntelliJ plugin development.

## Project Setup

- Gradle with IntelliJ Platform Plugin 2.10.5
- Kotlin 1.9.25 with Java 21 toolchain
- Requires Java 17+ to build (IntelliJ JBR works well)
- `instrumentationTools()` needed for proper plugin instrumentation

## Language Support

- `Language` class is fundamental - use singleton pattern for consistent reference
- `LanguageFileType` associates file extensions with the language
- `PsiFileBase` is the standard base for PSI files
- `FileViewProvider` bridges virtual files and PSI

## Parser & Lexer

- `IElementType` requires a Language instance to associate tokens
- `TokenSet.create()` groups related token types for parser use
- `@JvmField` annotation exposes Kotlin vals as Java fields
- `LexerBase` provides clean interface for custom lexers
- `PsiBuilder` marker pattern: `mark()`, `done(type)`, `drop()`, `precede()`
- `ParserDefinition` ties together lexer, parser, and PSI file creation
- `createElement()` is optional for minimal parsers
- Condition parsing must stop at LBRACE to avoid consuming the block
- Block parsing must skip whitespace before checking for RBRACE

## TextMate Integration

- TextMate extensions use namespace: `org.jetbrains.plugins.textmate`
- Extension point is `bundleProvider` (not `textmate.bundleProvider`)
- `TextMateBundleProvider` interface is in `org.jetbrains.plugins.textmate.api`
- `PluginBundle` takes name (String) and path (Path) to grammar directory
- Grammar uses `scopeName` for language identification (e.g., `source.elvish`)
- Oniguruma regex supports lookbehind `(?<![...])`
- Standard scope names map to theme colors (grammar assigns scopes, themes map colors)

## Settings & Configuration

- `@State` annotation's `storages` parameter specifies XML file name
- `@Service(Service.Level.PROJECT)` for project-scoped services
- `PersistentStateComponent` requires mutable properties for XML serialization
- `Project.getService()` retrieves service instance
- Kotlin UI DSL: `panel { row { ... } }` builder pattern
- `bindText()` connects UI component to property reference
- `projectConfigurable` uses `parentId="language"` for Languages & Frameworks
- Settings are read each time, so changes take effect on restart

## Notifications

- Notification groups registered in plugin.xml via `notificationGroup` extension
- `NotificationGroupManager.getInstance().getNotificationGroup()` retrieves groups
- `ConcurrentHashMap.putIfAbsent()` for "show once" logic
- `Project.locationHash` provides stable project identifier

## Icons & Marketplace

- JetBrains Marketplace requires 40x40 SVG icons with 2px padding
- `pluginIcon.svg` must be in META-INF folder (not /icons)
- SVG icons scale well in modern IDEs
- `IconLoader.getIcon` uses classpath-relative paths
- Plugin is "dynamic" - can be enabled/disabled without IDE restart

## Logging

- `Logger.getInstance()` with class reference is standard pattern
- `LOG.info()` writes to idea.log (Help > Show Log)
- `commandLineString` provides full command with arguments for diagnostics

## Gotchas

- TextMate namespace is `org.jetbrains.plugins.textmate` NOT `com.intellij`
- `com.intellij.util.Function` is deprecated - use `java.util.function.Function`
- `pluginIcon.svg` goes in META-INF folder, not /icons
- Condition parsing must stop at LBRACE or you'll consume the block body
- Block parsing must skip whitespace before RBRACE check
