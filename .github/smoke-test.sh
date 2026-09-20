#!/usr/bin/env bash
#
# File: .github/smoke-test.sh
#
# Start uacalc.jar and require that its window actually appears.
#
# A GUI does not exit, so "did it start?" cannot be answered by waiting and
# reading an exit status: a program that hangs while starting stays alive
# exactly as a healthy one does.  So this asks the X server instead, through
# xdotool, for a visible window whose name begins with UACalculator, which is
# the title UACalculator2 gives its frame.  Three things can go wrong, and
# each is reported as itself: the JVM exits, no window ever appears, or the
# window appears and the program then dies.
#
# Run it under a display.  In CI that is
#
#     xvfb-run -a .github/smoke-test.sh dist/lib/uacalc.jar
#
# and at a desk you can run it directly, where a window opens on your screen.
# The companion jars must sit beside the jar, as the build leaves them, since
# uacalc.jar's manifest names them by relative path.
#
# Usage: smoke-test.sh <path to uacalc.jar> [seconds to wait, default 60]

set -euo pipefail

jar=${1:?usage: smoke-test.sh <path to uacalc.jar> [seconds to wait]}
seconds=${2:-60}

java -jar "$jar" &
jvm=$!
trap 'kill "$jvm" 2>/dev/null || true' EXIT

id=""
for (( i = 1; i <= seconds; i++ )); do
  if ! kill -0 "$jvm" 2>/dev/null; then
    echo "FAIL: the JVM exited after ${i}s without opening a window." >&2
    exit 1
  fi
  id=$(xdotool search --onlyvisible --name '^UACalculator' 2>/dev/null | head -n 1 || true)
  if [ -n "$id" ]; then
    break
  fi
  sleep 1
done

if [ -z "$id" ]; then
  echo "FAIL: no UACalculator window after ${seconds}s; it started and then stalled." >&2
  exit 1
fi

echo "window $id after ${i}s: $(xdotool getwindowname "$id")"

# It has to survive its first few seconds too, so that dying on the first
# repaint is a failure rather than a pass.
sleep 5
if ! kill -0 "$jvm" 2>/dev/null; then
  echo "FAIL: the window appeared and the program then exited." >&2
  exit 1
fi
echo "still running five seconds later; $(java -version 2>&1 | head -n 1)"
