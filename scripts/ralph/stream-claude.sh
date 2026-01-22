#!/bin/bash
# Helper script for Ralph streaming
# Usage: stream-claude.sh <prompt-file> <output-file> <timeout>

set -e

PROMPT_FILE="$1"
OUTPUT_FILE="$2"
TIMEOUT="${3:-1800}"

# jq filters
STREAM_TEXT='select(.type == "assistant").message.content[]? | select(.type == "text").text // empty | gsub("\n"; "\r\n") | . + "\r\n\n"'
FINAL_RESULT='select(.type == "result").result // empty'

# Run Claude with streaming, capture output
timeout "$TIMEOUT" claude --dangerously-skip-permissions --verbose --print --output-format stream-json < "$PROMPT_FILE" 2>&1 \
  | grep --line-buffered "^{" \
  | tee "$OUTPUT_FILE" \
  | jq --unbuffered -rj "$STREAM_TEXT" 2>/dev/null || true

# Extract final result text for signal detection
jq -rs "$FINAL_RESULT" "$OUTPUT_FILE" > "$OUTPUT_FILE.result" 2>/dev/null || true
