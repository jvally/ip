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
