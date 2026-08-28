#!/usr/bin/env python3
"""Keep the existing test command as a compatibility wrapper around Gradle's JUnit task.

Select Java 25.0.3.fx-zulu first. Extra arguments are forwarded to the Gradle wrapper,
for example --tests friday.parser.ParserTest or --rerun-tasks.
"""

from pathlib import Path
import os
import subprocess
import sys


def main():
    """Run the single JUnit suite and propagate Gradle's exit code to callers."""
    repo = Path(__file__).resolve().parents[1]
    wrapper = repo / ("gradlew.bat" if os.name == "nt" else "gradlew")
    return subprocess.run([str(wrapper), "test", *sys.argv[1:]], cwd=repo).returncode


if __name__ == "__main__":
    raise SystemExit(main())
