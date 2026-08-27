# Friday User Guide

Friday is a command-line chatbot for managing tasks.

## Quick start

Type a command and press Enter. Friday supports todos, deadlines, events, and a few helper commands.

## Adding todos

Use `todo` for tasks with no date or time.

Example:
```text
todo borrow book
```

Friday adds the task as a todo item.

## Adding deadlines

Use `deadline` when a task has a due date.

Example:
```text
deadline return book /by Sunday
```

Friday stores both the description and the deadline text.

## Adding events

Use `event` for tasks with a start and end time.

Example:
```text
event project meeting /from Mon 2pm /to 4pm
```

Friday stores the description, start time, and end time.

## Listing tasks

Use `list` to show all current tasks.

Example:
```text
list
```

Friday shows each task with its number. Use that number for `mark`, `unmark`, and `delete`.

## Marking tasks

Use `mark` to mark a task as done.

Example:
```text
mark 2
```

Use `unmark` to change it back to not done.

Example:
```text
unmark 2
```

## Deleting tasks

Use `delete` to remove a task from the list.

Example:
```text
delete 3
```

## Saving and loading

Run Friday with the project root as the working directory (including in IntelliJ).
Tasks load automatically at startup and save after each addition, deletion, or completion-status change
to `data/friday.txt`, relative to that directory. No save command or `bye` is required.
Friday creates the `data` folder and file on the first change if they do not exist.
Local task data is excluded from Git.

The UTF-8 text format uses one task per line, with `0` for not done and `1` for done:

```text
T|1|read book
D|0|return book|Sunday
E|0|project meeting|Mon 2pm|4pm
```

Fields are separated by `|` with no added spaces. Within text fields, `\|` means a literal pipe,
`\\` a backslash, `\n` a newline, and `\r` a carriage return. Empty files represent empty lists.

If the file is malformed or unreadable, Friday starts with an empty session list and displays a warning.
Saving stays disabled for that session to protect the original file; any new changes remain only in memory.
Back up and repair the file (or move it aside to start fresh), then restart Friday.
If a save fails, Friday warns you and keeps the session running. Fix the folder or file permissions;
the next task-list change retries saving the full list. Successful saves write a temporary snapshot before
replacing the old file, using an atomic move when the filesystem supports it.

## Friendly commands

Friday also responds to a few personality commands:

- `hello`
- `thanks`
- `help`
- `bye`

## Error handling

Friday gives clear messages for invalid input. For example, it handles:

- empty `todo` descriptions
- unknown commands
- missing deadline or event details
- missing or invalid task numbers for `mark`, `unmark`, and `delete`

This keeps the chatbot from silently accepting bad input.
