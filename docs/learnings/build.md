# Build & Environment

Learnings about Gradle, build configuration, and environment setup.

## Gradle Configuration

- IntelliJ Platform Plugin 2.10.5
- Kotlin 1.9.25 with Java 21 toolchain
- `untilBuild.set(provider { null })` removes upper version bound
- Plugin verifier: `intellijPlatform.pluginVerification.ides { recommended() }`
- `bundledPlugin()` in intellijPlatform dependencies for bundled plugins
- Gradle wrapper ensures consistent builds across environments
- Environment variables for signing: CERTIFICATE_CHAIN, PRIVATE_KEY, etc.

## Build Commands

```bash
./gradlew build      # Build and run tests
./gradlew runIde     # Launch sandbox IDE
./gradlew buildPlugin  # Create distribution ZIP
./gradlew verifyPlugin # Run plugin verifier
```

## Java Requirements

- Requires Java 17+ to build
- IntelliJ JBR (bundled runtime) works well
- JAVA_HOME must be set if default Java is lower than 17

## macOS Specifics

- Use `stat -f%z` instead of `wc -c` for file size (more reliable in pipes)
- Homebrew installs elvish to `/opt/homebrew/bin/elvish`
- JBR path: `/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home`

## Plugin Verification

- Verify against multiple IDE versions before release
- LSP API marked as experimental - warnings expected
- Plugin is "dynamic" - can be enabled/disabled without restart

## Gotchas

- Don't use `wc -c` in Elvish pipes - can fail silently, use `stat -f%z`
- JAVA_HOME must point to Java 17+ or build fails
- `instrumentationTools()` is required for proper plugin instrumentation
- Build range: sinceBuild 243 (2024.3), untilBuild open-ended
