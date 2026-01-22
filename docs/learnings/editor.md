# Editor Features

Learnings about editor enhancements (folding, structure view, breadcrumbs, etc.).

## Commenter

- `Commenter` interface has 5 methods: getLineCommentPrefix, getBlockCommentPrefix/Suffix, getCommentedBlockCommentPrefix/Suffix
- For languages without block comments, return null for block-related methods
- Using "# " (with space) provides better readability when toggling comments

## Brace Matching

- `PairedBraceMatcher` uses `BracePair(left, right, structural)` to define pairs
- The "structural" flag (3rd param) indicates if braces define code blocks for folding
- `isPairedBracesAllowedBeforeType` controls auto-insertion of closing brackets
- Brace matching relies on token types from the lexer

## Code Folding

- `FoldingBuilderEx` provides more control than `FoldingBuilder`
- `DumbAware` interface allows folding to work during indexing
- `FoldingGroup` groups related fold regions together
- Only multi-line constructs should be foldable
- Context detection via looking backward in PSI tree to find preceding keywords

## Structure View

- `PsiStructureViewFactory` creates `TreeBasedStructureViewBuilder` instances
- `StructureViewModelBase` provides base implementation with sorting support
- `StructureViewTreeElement` wraps PSI elements for tree display
- `SortableTreeElement` interface enables alpha sorting
- `AllIcons.Nodes.*` provides standard icons (Function, Variable, Include)
- Parsing tokens from statements to find fn/var/use keywords works with flat AST

## Breadcrumbs

- `BreadcrumbsProvider` interface: `getLanguages()`, `acceptElement()`, `getElementInfo()`, `getElementTooltip()`
- IntelliJ framework handles nested breadcrumbs by traversing parent elements automatically
- Navigation and enable/disable settings are handled by the framework
- Lambda expressions distinguished by `{|` pattern at start of block

## TODO/FIXME Highlighting

- `IndexPatternBuilder` interface enables TODO patterns (TODO, FIXME, XXX, HACK, BUG) in comments
- Key methods: `getIndexingLexer()`, `getCommentTokenSet()`, `getCommentStartDelta()`, `getCommentEndDelta()`
- `getCommentStartDelta()` returns 1 for `#` comments to skip the hash character
- `getCommentEndDelta()` returns 0 for line comments (no closing delimiter)
- `ParserDefinition.getCommentTokens()` must return the comment token set for basic TODO support
- `indexPatternBuilder` extension point registered in plugin.xml
- TODO items appear in View > Tool Windows > TODO

## Spell Checking

- `SpellcheckingStrategy` controls which tokens are checked for spelling
- `getTokenizer()` returns `TEXT_TOKENIZER` to enable spell checking, `EMPTY_TOKENIZER` to disable
- Check element type against token types to determine spell checking eligibility
- Comments and strings should be checked; barewords/identifiers should NOT (they're often commands/paths)
- Register with `spellchecker.support` extension point in plugin.xml

## Gotchas

- Editor features should be in dedicated `editor` package
- Single-line blocks are not useful to fold - skip them
- Structure view only shows top-level declarations, not nested
- Similar token collection pattern works for both structure view and breadcrumbs
- TODO indexing requires both ParserDefinition.getCommentTokens() AND IndexPatternBuilder for full support
- Spell checking for barewords/identifiers should be disabled (they're commands, paths, variable names)
