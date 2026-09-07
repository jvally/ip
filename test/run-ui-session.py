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
    contact_file = working_directory / "data" / "contacts.txt"
    records = []
    contact_records = []
    commands = []
    block_save = False
    block_contact_save = False
    for line in lines:
        if line.startswith("@file "):
            records.append(line.removeprefix("@file "))
        elif line.startswith("@contact-file "):
            contact_records.append(line.removeprefix("@contact-file "))
        elif line == "@contact-directory":
            contact_file.mkdir(parents=True)
        elif line == "@block-contact-save":
            block_contact_save = True
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
    if contact_records:
        contact_file.parent.mkdir(parents=True, exist_ok=True)
        contact_file.write_text("\n".join(contact_records) + "\n", encoding="utf-8")

    process = subprocess.Popen(
        ["java", "-cp", str(build), "friday.Friday"],
        cwd=working_directory,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
    )
    if block_save or block_contact_save:
        # Wait for startup to finish before making the save destination unwritable.
        # A nonempty directory works without relying on OS permission bits or root status.
        separator_count = 0
        while separator_count < 2:
            line = process.stdout.readline()
            if not line:
                raise RuntimeError("Friday exited before finishing its welcome message.")
            print(line, end="")
            if line.strip() == "_" * 60:
                separator_count += 1
        # A seeded save has already been loaded; replace only this test's temporary file.
        for destination, should_block in ((data_file, block_save), (contact_file, block_contact_save)):
            if should_block:
                if destination.is_file():
                    destination.unlink()
                destination.mkdir(parents=True, exist_ok=True)
                (destination / "blocker").write_text("Keep this directory nonempty.", encoding="utf-8")
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
