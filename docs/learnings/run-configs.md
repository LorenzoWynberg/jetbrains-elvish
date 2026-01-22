# Run Configurations

Learnings about run configurations and script execution.

## Configuration Type

- `RunConfigurationBase<Options>` separates configuration logic from state persistence
- `StoredProperty` with `provideDelegate` provides type-safe property storage
- `SimpleConfigurationType` handles factory creation, icon, and registration boilerplate
- Environment variables can be serialized as semicolon-separated key=value pairs
- `checkConfiguration()` uses `RuntimeConfigurationError` for required fields, `RuntimeConfigurationWarning` for recommendations

## Settings Editor

- `EnvironmentVariablesTextFieldWithBrowseButton` is preferred for environment variable editing
- `RawCommandLineEditor` handles argument parsing, quoting, and escaping automatically
- UI components can be enabled/disabled dynamically via `addActionListener` on checkboxes
- Working directory defaults to `project.basePath` if not explicitly set

## Execution

- `CommandLineState` provides standard run infrastructure (console, actions, etc.)
- `KillableColoredProcessHandler` displays colored stdout/stderr and supports stop button
- `ProcessTerminatedListener` automatically displays exit code when process finishes
- Custom argument parser needed for proper handling of quoted strings

## Gutter Icons

- `RunLineMarkerContributor.Info` with `java.util.function.Function` (not deprecated `com.intellij.util.Function`)
- `ExecutorAction.getActions(0)` provides standard run actions for the file
- Show marker only at first leaf element to avoid duplicate icons in gutter
- `findFirstLeafElement()` traverses PSI tree to find first token
- `AllIcons.RunConfigurations.TestState.Run` is the standard green play icon

## Configuration Producer

- `LazyRunConfigurationProducer` creates configuration factory lazily for better performance
- `isConfigurationFromContext()` enables reuse of existing configurations matching the same script
- `setupConfigurationFromContext()` should set script path, name, and working directory
- Working directory defaults to script's parent directory for better UX
- Configuration name uses `nameWithoutExtension` for cleaner display

## Gotchas

- Run configurations go in dedicated `run` package
- `getElvishPath()` must check `useElvishFromSettings` flag to decide source
- Show gutter marker only at FIRST leaf element or you get duplicates
- Default elvish path is just "elvish" (expects it in PATH)
