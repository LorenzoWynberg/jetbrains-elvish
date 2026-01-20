# Ralph Agent - Story {{CURRENT_STORY}}

## Context
Story: {{CURRENT_STORY}} | Branch: {{BRANCH}} | Attempt: {{ATTEMPT}} | Iteration: {{ITERATION}}/{{MAX_ITERATIONS}}

Dependencies (completed): {{DEPENDENCIES}}

## 1. Load Context
Read: `scripts/ralph/prd.json` (acceptance criteria), `scripts/ralph/progress.txt` (patterns), `CLAUDE.md` (conventions)

If attempt > 1: check `git log` and `git diff` for previous work.

## 2. Scope
Implement ONLY {{CURRENT_STORY}}. No refactoring unrelated code. Note other issues in Learnings only.

## 3. Implementation
- Write tests when applicable (unit tests for logic, integration tests for features)
- Tests go in `src/test/kotlin/` mirroring the main source structure
- Skip tests only if: pure UI code, trivial getters/setters, or external integration (LSP)

## 4. Verification (Required)
```bash
./gradlew build  # Must pass (includes compile and tests)
```

## 5. Self-Review (Max 3 cycles)
After build passes, ask: "What's missing or could improve?"
- Edge cases, API design, code organization, error handling
- Only implement if: in scope, meaningful, aligns with acceptance criteria
- Atomic commits per fix, re-run build after each

## 6. Refactor Check (2 rounds required before merge)
After self-review, perform **two rounds** of refactor checking to catch issues missed in the first pass.

**For each round (1 and 2), check for these issues:**
- **File size**: Is any file > 300 lines? Consider splitting
- **Code duplication**: Are there repeated code blocks that should be extracted into shared functions?
- **Mixed responsibilities**: Does a file handle multiple unrelated concerns?
- **Module organization**: Each feature area should be in its own package (e.g., `lsp/`, `settings/`, `highlighting/`)
- **Re-exports**: Update package visibility to cleanly expose public APIs
- **Dead code**: Remove any unused code introduced in this story

**If refactoring needed in either round:**
1. Split large files into focused modules
2. Move implementations to dedicated packages
3. Update imports and package structure
4. Run `./gradlew build` - must pass
5. Commit refactor separately: `git commit -m "refactor: organize {{CURRENT_STORY}} code structure"`
6. **Continue to next round** (or complete if on round 2)

**Skip refactoring for a round if:**
- Changes are minimal (< 50 lines added)
- Code is already well-organized
- Splitting would create artificial boundaries

**Round 2 focus:** Double-check that round 1 refactoring didn't introduce new issues (e.g., large files, missed exports, circular dependencies).

## 7. Documentation Update (Required before merge)
Update docs if your changes affect any of the following:

- **README.md** - New features, changed requirements, installation steps
- **docs/DEVELOPMENT.md** - Architecture changes, new components, build process
- **docs/CONTRIBUTING.md** - Workflow changes, new conventions
- **CLAUDE.md** - Key files changed, new patterns

**Always update if:**
- Adding new files/packages → update Architecture section
- Changing build process → update Build Commands
- Adding new feature → update Features list in README

**Skip only if:** Changes are purely internal with no user-facing or developer-facing impact.

Commit docs separately: `git commit -m "docs: update for {{CURRENT_STORY}}"`

## 8. On SUCCESS

Update these files:
- `progress.txt`: Add entry with date, changes, learnings (include any refactoring done)
- `prd.json`: Set `"passes": true`
- `scripts/ralph/state.json`: Reset to `{"version":1,"current_story":null,"status":"idle","branch":null,"started_at":null,"last_updated":null,"attempts":0,"error":null,"checkpoints":[]}`

Then:
```bash
git add -A && git commit -m "feat: {{CURRENT_STORY}} - <title>"
git push -u origin {{BRANCH}}
gh pr create --base dev --title "feat: {{CURRENT_STORY}} - <title>" --body "## Summary
<what>
## Changes
- <list>
## Refactoring
- <any code organization changes, or 'None needed'>
## Testing
- ./gradlew build passes"
gh pr merge --merge --delete-branch
git checkout dev && git pull origin dev
git branch -d {{BRANCH}} 2>/dev/null || true
```

Output: `<story-complete>{{CURRENT_STORY}}</story-complete>`

## 9. On BLOCKED
Don't commit. Don't update prd.json. Add to progress.txt what was tried and why blocked.

Output: `<story-blocked>{{CURRENT_STORY}}</story-blocked>`

## 10. All Done?
If ALL stories in prd.json have `"passes": true`, output: `<promise>COMPLETE</promise>`
