# Contributing Guide

## Branching Strategy (GitHub Flow)

- **main** - Single integration branch, all PRs merge here
- **feat/description** - Feature branches (from main)
- **feat/story-X.Y.Z** - Ralph story branches (automated)
- **fix/description** - Bug fix branches (from main)

### Flow

```
feat/* ──► main (tag vX.Y.Z for releases)
fix/*  ──┘
```

Releases are tagged directly on main (e.g., `v1.0.0`).

## Workflow

1. Create feature branch from `main`
2. Implement changes
3. Run `./gradlew build` to verify
4. Create PR targeting `main`
5. After merge, feature branch is deleted

## Commit Guidelines

- One commit per logical change
- Push after each commit
- Keep messages concise and descriptive
- **No "Co-Authored-By" lines** (commits or PRs)

### Commit Message Format

```
type: STORY-XXX - Short description

Optional longer description if needed.
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `refactor` - Code restructuring
- `docs` - Documentation only
- `chore` - Maintenance tasks

**Examples:**
```
feat: STORY-010 - Create TextMate grammar for Elvish
fix: STORY-012 - Handle missing elvish binary gracefully
docs: Update README with installation instructions
```

## Pull Requests

- PRs always target `main` branch
- Include a brief description of changes
- Reference the story ID if applicable
- Ensure `./gradlew build` passes

## Code Style

- Follow Kotlin conventions
- Use meaningful variable/function names
- Keep functions focused and small
- Add comments only where logic isn't self-evident

## Testing Changes

Before submitting a PR:

1. Run `./gradlew build` - must pass
2. Run `./gradlew runIde` - manually test the feature
3. Test with various `.elv` files

## Automated Development

This project uses [Ralph](docs/ralph/RALPH.md) for autonomous development. If you're contributing manually, you can ignore Ralph and follow the standard workflow above.
