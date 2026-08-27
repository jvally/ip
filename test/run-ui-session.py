#!/usr/bin/env python3
"""Run one UI case in an isolated directory, with optional fixtures and restarts.

The test-ui skill supplies stdin and compares stdout against test/ui-test-plan.md.
Lines beginning with @ are test setup directives, never chatbot commands.
"""

import subprocess
import sys
import tempfile
from pathlib import Path


def run_session(build, working_directory, lines):
    """Set up the session, then run Friday until EOF without losing saved data."""
    data_file = working_directory / "data" / "friday.txt"
    records = []
    commands = []
    block_save = False
    for line in lines:
        if line.startswith("@file "):
            records.append(line.removeprefix("@file "))
        elif line == "@directory":
            data_file.mkdir(parents=True)
        elif line == "@block-save":
            block_save = True
        elif line.startswith("@"):
            raise ValueError("Unknown test directive: " + line)
        else:
            commands.append(line)
    if records:
        data_file.parent.mkdir(parents=True, exist_ok=True)
        data_file.write_text("\n".join(records) + "\n", encoding="utf-8")

    process = subprocess.Popen(
        ["java", "-cp", str(build), "friday.Friday"],
        cwd=working_directory,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
    )
    if block_save:
        # Wait for startup to finish before making the save destination unwritable.
        # A nonempty directory works without relying on OS permission bits or root status.
        for _ in range(4):
            print(process.stdout.readline(), end="")
        # A seeded save has already been loaded; replace only this test's temporary file.
        if data_file.is_file():
            data_file.unlink()
        data_file.mkdir(parents=True)
        (data_file / "blocker").write_text("Keep this directory nonempty.", encoding="utf-8")
    output, errors = process.communicate("\n".join(commands) + "\n", timeout=15)
    print(output, end="")
    print(errors, end="", file=sys.stderr)
    if process.returncode:
        raise SystemExit(process.returncode)


def main():
    """Compile once per test case and preserve its isolated data across @restart."""
    repo = Path(__file__).resolve().parents[1]
    lines = sys.stdin.read().splitlines()
    with tempfile.TemporaryDirectory(prefix="friday-ui-") as temporary:
        working_directory = Path(temporary)
        build = working_directory / "classes"
        subprocess.run(
            ["javac", "-d", str(build)]
            + [str(source) for source in sorted((repo / "src/main/java").rglob("*.java"))],
            check=True,
        )
        session = []
        for line in lines:
            if line == "@restart":
                run_session(build, working_directory, session)
                session = []
            else:
                session.append(line)
        run_session(build, working_directory, session)


if __name__ == "__main__":
    main()
