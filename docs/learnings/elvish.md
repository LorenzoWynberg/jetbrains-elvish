# Elvish Language

Learnings about the Elvish shell language.

## Syntax

- Uses `catch e` syntax where `e` is the error variable (not `catch (e)`)
- For loops use `for var container` syntax (no `in` keyword)
- Allows hyphens in identifiers (e.g., `my-function`)
- Lambda expressions use `{|params| body}` syntax
- Function parameters extracted from `{|param1 param2|}` syntax

## Features

- Rational numbers (e.g., 22/7) are valid numeric literals
- `$@` explode syntax for list expansion
- String comparison operators use `*s` variants (e.g., `<s`, `>s`)
- Modules don't need shebang (imported via `use`, not executed directly)
- Variables inside strings handled via interpolation

## Operators

- Range operator: `1..10` (must distinguish from float `1.5`)
- Lexer must check for digit after `.` to distinguish float from range
- `>>` redirect operator is context-dependent

## Gotchas

- Don't forget: `catch e` not `catch (e)` - Elvish isn't JavaScript
- Don't forget: `for x $list` not `for x in $list` - no `in` keyword
- Lexer trap: `1..10` looks like float `1.` followed by `.10` if you're not careful
- Rational numbers `22/7` are FLOAT tokens, not division
