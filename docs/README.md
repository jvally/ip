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
deadline return book /by 2019-12-02
```

Friday stores the deadline as a `LocalDateTime` and displays `Dec 02 2019`.

## Adding events

Use `event` for tasks with a start and end time.

Example:
```text
event project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00
```

Friday stores the start and end as `LocalDateTime` values. The end cannot be before the start;
equal endpoints are allowed. Give a full date for both endpoints, including events within one day.

## Dates and times

Deadlines and event endpoints accept these formats:

| Input format | Example | Display |
| --- | --- | --- |
| `yyyy-MM-dd` | `2019-10-15` | `Oct 15 2019` |
| `yyyy-MM-dd HH:mm` | `2019-12-02 18:00` | `Dec 02 2019, 18:00` |
| `d/M/yyyy HHmm` | `2/12/2019 1800` | `Dec 02 2019, 18:00` |

Slash-separated dates are day/month/year, so `2/12/2019` means 2 December.
Times use the 24-hour clock. Date-only input means midnight; midnight is displayed as a date alone.
These are local dates and times without a timezone. Month names always display in English.
Invalid dates (such as `2019-02-29`), invalid times (such as `24:00`), and vague dates (such as
`Sunday`) produce an error without adding a task. The ISO storage form `2019-12-02T18:00` is also accepted.

## Listing tasks

Use `list` to show all current tasks.

Example:
```text
list
```

Friday shows each task with its number. Use that number for `mark`, `unmark`, and `delete`.

## Finding tasks on a date

Use `on yyyy-MM-dd` to show deadlines and events on a specific date:

```text
on 2019-12-02
```

Deadlines match their due date. Events match every calendar date from their start date through their
end date, including both boundary dates (even an end at midnight). Completed tasks are included;
undated todos are excluded. The displayed numbers are the original list numbers, so they still work
with `mark`, `unmark`, and `delete`. This command does not change or save the task list.

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
D|0|return book|2019-12-02T00:00
E|0|project meeting|2019-12-02T14:00|2019-12-02T16:00
```

Fields are separated by `|` with no added spaces. Within text fields, `\|` means a literal pipe,
`\\` a backslash, `\n` a newline, and `\r` a carriage return. Empty files represent empty lists.
Date fields use ISO date-time values with minute precision, independently of the console display.

Older Level 7 records still load when their date fields match a supported format.
Text such as `Sunday` or `Mon 2pm` cannot be converted without guessing a date.
If your save contains such text, back up the file and replace those fields with explicit dates
before restarting. Friday will preserve an unreadable or invalid original file as described below.

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
