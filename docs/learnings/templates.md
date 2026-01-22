# Templates

Learnings about file templates and live templates.

## File Templates

- Templates use `.ft` suffix and are placed in `fileTemplates/` resource directory
- `CreateFileFromTemplateAction` provides base functionality for template-based file creation
- `internalFileTemplate` extension makes templates available to the IDE
- Action must be added to `NewGroup` to appear in "New" context menu
- Template variables use `${VARIABLE_NAME}` syntax
- `addKind()` in `buildDialog()` allows multiple template options in same action

## Live Templates

- `liveTemplateContext` extension requires `contextId` matching XML context name
- Template variables use `alwaysStopAt="true"` for Tab navigation
- `$END$` is special variable for final cursor position after expansion
- Use `&#10;` for newlines in XML value attribute
- Default values help users understand expected input format
- `ElvishTemplateContextType` checks `file.language == ElvishLanguage.INSTANCE`

## Template Content

- Script templates include shebang (`#!/usr/bin/env elvish`)
- Module templates DON'T need shebang (imported, not executed)
- Include usage instructions as comments for user convenience
- Description HTML should explain the language feature

## Gotchas

- Templates go in dedicated `templates` package
- Context name in XML must EXACTLY match `contextId` in plugin.xml
- `toReformat="false"` preserves Elvish formatting in live templates
- Template default values are shown in completion popup - make them useful
