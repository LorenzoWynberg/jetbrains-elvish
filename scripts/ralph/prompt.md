# Ralph Agent - Story {{CURRENT_STORY}}

## Context
Story: {{CURRENT_STORY}} | Branch: {{BRANCH}} | Attempt: {{ATTEMPT}} | Iteration: {{ITERATION}}/{{MAX_ITERATIONS}}

Dependencies (completed): {{DEPENDENCIES}}

## 1. Load Context
Read: `scripts/ralph/prd.json` (acceptance criteria), `scripts/ralph/progress.txt` (patterns), `CLAUDE.md` (conventions)

If attempt > 1: check `git log` and `git diff` for previous work.

## 2. Scope
Implement ONLY {{CURRENT_STORY}}. No refactoring unrelated code. Note other issues in Learnings only.

## 3. Verification (Required)
```bash
./gradlew build  # Must pass (includes compile and tests)
```

## 4. Self-Review (Max 3 cycles)
After build passes, ask: "What's missing or could improve?"
- Edge cases, API design, code organization, error handling
- Only implement if: in scope, meaningful, aligns with acceptance criteria
- Atomic commits per fix, re-run build after each

## 5. Refactor Check (Required before merge)
After self-review, evaluate code organization:

**Check for these issues:**
- **File size**: Is any file > 300 lines? Consider splitting
- **Mixed responsibilities**: Does a file handle multiple unrelated concerns?
- **Module organization**: Each feature area should be in its own package
- **Dead code**: Remove any unused code introduced in this story

**If refactoring needed:**
1. Split large files into focused modules
2. Update imports and package structure
3. Run `./gradlew build` - must pass
4. Commit refactor separately: `git commit -m "refactor: organize {{CURRENT_STORY}} code structure"`

**Skip refactoring if:**
- Changes are minimal (< 50 lines added)
- Code is already well-organized
- Splitting would create artificial boundaries

## 6. On SUCCESS

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

## 7. On BLOCKED
Don't commit. Don't update prd.json. Add to progress.txt what was tried and why blocked.

Output: `<story-blocked>{{CURRENT_STORY}}</story-blocked>`

## 8. All Done?
If ALL stories in prd.json have `"passes": true`, output: `<promise>COMPLETE</promise>`
