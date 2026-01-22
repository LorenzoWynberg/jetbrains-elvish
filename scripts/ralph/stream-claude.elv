#!/usr/bin/env elvish
# Helper script for Ralph streaming
# Usage: elvish stream-claude.elv <prompt-file> <output-file> <timeout>

var prompt-file = $args[0]
var output-file = $args[1]
var timeout-secs = (if (> (count $args) 2) { put $args[2] } else { put "1800" })

# jq filters - defined here to avoid quoting issues in parent script
var stream-text = 'select(.type == "assistant").message.content[]? | select(.type == "text").text // empty | gsub("\n"; "\r\n") | . + "\r\n\n"'
var final-result = 'select(.type == "result").result // empty'

# Run Claude with streaming, capture output
try {
  timeout $timeout-secs bash -c 'claude --dangerously-skip-permissions --verbose --print --output-format stream-json < "$1" 2>&1 | grep --line-buffered "^{" | tee "$2" | jq --unbuffered -rj "$3" 2>/dev/null || true' _ $prompt-file $output-file $stream-text
} catch _ {
  # Timeout or error - continue
}

# Extract final result text for signal detection
try {
  jq -rs $final-result $output-file > $output-file".result" 2>/dev/null
} catch _ { }
