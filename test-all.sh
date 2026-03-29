#!/usr/bin/env bash
# Run all test tiers in sequence.
# Usage:
#   ./test-all.sh               — all tiers
#   ./test-all.sh --skip-client — skip client render tests (headless-safe on Windows)
set -e
cd "$(dirname "$0")/java-edition"

echo "=== Tier 1: JUnit unit tests ==="
./gradlew test

echo "=== Tier 2+3: GameTests (in-world, headless server) ==="
if command -v xvfb-run &>/dev/null; then
  xvfb-run -a ./gradlew runGametest
else
  # Windows or systems without Xvfb — run directly
  ./gradlew runGametest
fi

if [[ "$1" != "--skip-client" ]]; then
  echo "=== Tier 4: Client rendering tests ==="
  # NOTE: runClientGametest requires Fabric Client GameTest API wired up first.
  # Verify the task exists: ./gradlew tasks --group=fabric
  # On Linux CI, run with: xvfb-run -a -s "-screen 0 1280x720x24" LIBGL_ALWAYS_SOFTWARE=1 ./gradlew runClientGametest
  if command -v xvfb-run &>/dev/null; then
    LIBGL_ALWAYS_SOFTWARE=1 xvfb-run -a -s "-screen 0 1280x720x24" ./gradlew runClientGametest
  else
    ./gradlew runClientGametest
  fi
fi

echo "=== All tests passed ==="
