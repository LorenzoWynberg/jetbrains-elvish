# Ralph.elv Future Improvements Plan

This document outlines potential improvements for ralph.elv to enhance robustness and usability.

## Completed Fixes

The following critical bugs have been fixed in PR #37:
- Line 505: Fixed pipe syntax for `str:trim-space`
- Lines 153-159: Changed to use `has-external` instead of `command -v`
- Line 231: Added validation for empty split results
- ElvishRunConfiguration.kt: Added `getOptionsClass()` override

---

## Planned Improvements

### 1. Add Timeout for Claude Execution

**Priority**: High
**Rationale**: Prevents indefinite hangs if Claude gets stuck

```elvish
# Current (can hang indefinitely):
echo $iteration-prompt | claude --dangerously-skip-permissions --print > $output-file 2>&1

# Improved (with timeout):
try {
  timeout 1800 bash -c 'echo "$1" | claude --dangerously-skip-permissions --print' _ $iteration-prompt > $output-file 2>&1
} catch {
  ralph-error "Claude timed out after 30 minutes"
  # Handle timeout...
}
```

### 2. State File Initialization Improvement

**Priority**: Medium
**Rationale**: More idiomatic Elvish, less error-prone

```elvish
# Current (using echo):
echo '{"version":1,...}' > $state-file

# Improved:
var initial-state = [
  &version=(num 1)
  &current_story=$nil
  &status="idle"
  &branch=$nil
  &started_at=$nil
  &last_updated=$nil
  &attempts=(num 0)
  &error=$nil
  &checkpoints=[]
]
put $initial-state | to-json > $state-file
```

### 3. Better Error Messages for jq Failures

**Priority**: Medium
**Rationale**: Easier debugging when story lookups fail

```elvish
fn get-story-info {|story-id|
  var sid = $story-id
  var pf = $prd-file
  var query = ".stories[] | select(.id == \""$sid"\") | \"\\(.phase)\\t\\(.epic)\\t\\(.story_number)\""
  var result = (jq -r $query $pf | slurp | str:trim-space)
  if (eq $result "") {
    fail "Story "$sid" not found in PRD"
  }
  put $result
}
```

### 4. Branch Existence Check - Include Remote

**Priority**: Medium
**Rationale**: Avoid creating duplicate branches when remote already has one

```elvish
fn branch-exists {|branch|
  try {
    # Check local
    git -C $project-root rev-parse --verify "refs/heads/"$branch > /dev/null 2>&1
    put $true
  } catch {
    try {
      # Check remote
      git -C $project-root rev-parse --verify "refs/remotes/origin/"$branch > /dev/null 2>&1
      put $true
    } catch {
      put $false
    }
  }
}
```

---

## Feature Improvements (Lower Priority)

### 5. Add Dry-Run Mode

```elvish
var dry-run = $false

# In arg parsing:
} elif (eq $arg "--dry-run") {
  set dry-run = $true
  set i = (+ $i 1)

# Usage:
if $dry-run {
  ralph-status "DRY RUN: Would create branch "$branch-name
} else {
  git -C $project-root checkout -b $branch-name > /dev/null 2>&1
}
```

### 6. Add Story Skip Functionality

```elvish
var skip-story = ""

# In arg parsing:
} elif (eq $arg "--skip") {
  var next-idx = (+ $i 1)
  set skip-story = $args[$next-idx]
  set i = (+ $i 2)

# In get-next-story, filter out skipped story
```

### 7. Improve Progress Reporting

```elvish
fn show-progress {
  var pf = $prd-file
  var total = (jq '.stories | length' $pf)
  var complete = (jq '[.stories[] | select(.passes == true)] | length' $pf)
  var pct = (/ (* $complete 100) $total)
  ralph-status "Progress: "$complete"/"$total" stories ("$pct"%)"
}
```

### 8. Add Checkpoint/Rollback

```elvish
fn save-checkpoint {|name|
  var checkpoint = [
    &name=$name
    &timestamp=(date -u '+%Y-%m-%dT%H:%M:%SZ')
    &commit=(git -C $project-root rev-parse HEAD | slurp | str:trim-space)
    &branch=(current-branch)
  ]
  var state = (read-state)
  set state[checkpoints] = [$@state[checkpoints] $checkpoint]
  write-state $state
  ralph-success "Checkpoint saved: "$name
}

fn rollback-to-checkpoint {|name|
  var state = (read-state)
  for cp $state[checkpoints] {
    if (eq $cp[name] $name) {
      git -C $project-root reset --hard $cp[commit]
      ralph-success "Rolled back to checkpoint: "$name
      return
    }
  }
  ralph-error "Checkpoint not found: "$name
}
```

---

## Potential Issues to Monitor

### PRD Dependency Cycles
If the PRD has circular dependencies, `get-next-story` will loop forever returning `$nil`. Consider adding cycle detection with topological sort.

### Git State Corruption
If Claude crashes mid-commit, git state could be corrupted. Consider adding:
```elvish
fn ensure-clean-git-state {
  var status = (git -C $project-root status --porcelain | slurp)
  if (not (eq $status "")) {
    ralph-warn "Git has uncommitted changes"
    # Optionally: git stash or abort
  }
}
```

### Prompt Template Variables
If `{{VARIABLE}}` placeholders in prompt.md don't all get replaced, Claude might be confused. Consider validating all replacements succeeded.

---

## Code Style Improvements

### Use `nop` for Intentionally Ignored Output
```elvish
# Instead of:
git fetch > /dev/null 2>&1

# Use:
git fetch 2>&1 | nop
```

### Consider Modular Structure
Split ralph.elv into modules for maintainability:
- `ralph-git.elv` - Git operations
- `ralph-state.elv` - State management
- `ralph-ui.elv` - Output formatting
- `ralph.elv` - Main entry point
