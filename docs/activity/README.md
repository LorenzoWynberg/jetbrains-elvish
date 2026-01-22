# Activity Logs

Daily development activity logs for the Elvish JetBrains plugin.

## Purpose

Activity logs track daily development work including:
- Features implemented
- Bugs fixed
- Decisions made
- Problems encountered and solutions
- Next steps identified

## File Format

Each day's activity is recorded in a file named `YYYY-MM-DD.md`:
- `2026-01-20.md` - Activity for January 20, 2026

## Template

```markdown
# Activity Log - YYYY-MM-DD

## Summary
Brief overview of what was accomplished today.

## Changes Made
- [ ] Change 1
- [ ] Change 2

## Files Modified
- `path/to/file.kt` - Description of changes

## Decisions Made
- Decision and rationale

## Issues Encountered
- Problem and how it was resolved

## Next Steps
- What needs to be done next
```

## Guidelines

1. **Create a new log** at the start of each development session
2. **Update throughout** the session as work progresses
3. **Be specific** about what changed and why
4. **Link to commits** when relevant
5. **Note blockers** and how they were resolved

## Archive

Older activity logs are moved to `archive/` to keep the main directory clean.

**Structure:**
```
docs/activity/
├── README.md           # This file
├── 2026-01-21.md       # Recent logs (keep last 7 days)
├── 2026-01-20.md
└── archive/
    └── 2026-01/        # Monthly folders
        └── 2026-01-15.md
```

**When to archive:** Move logs older than 7 days to `archive/YYYY-MM/` folder.

**To archive manually:**
```bash
mkdir -p docs/activity/archive/2026-01
mv docs/activity/2026-01-15.md docs/activity/archive/2026-01/
```

## Automation

When using Claude Code or Ralph, activity logs should be created/updated automatically at the start of each session. The log serves as a record of AI-assisted development work.
