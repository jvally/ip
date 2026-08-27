# UI Test Plan

This file is the source of truth for command-driven UI regression tests.
Update it whenever the console UI changes.

Program command: `python3 test/run-ui-session.py`

Run the test-ui skill with Java 25.0.3.fx-zulu selected using `sdk use java 25.0.3.fx-zulu`.
The session helper compiles and runs each case in its own temporary directory, so tests never
read or overwrite real task data. The original cases also test startup without a data folder.
The following test-only directives in Inputs are consumed by the helper, not sent to Friday:

- `@restart` ends the current input stream (EOF) and starts Friday again with the same data folder.
- `@file RECORD` supplies a line of the initial save file before startup.
- `@directory` creates a directory instead of the save file, to test a read failure.
- `@block-save` blocks the destination with a nonempty directory after startup, to test a write failure.

The helper does not supply expected output; all console expectations remain in this plan.

Storage-level checks (including malformed records, failed writes, and lists above 100 tasks)
can also be run with the same JDK from the project root:

```bash
javac -d /private/tmp/friday-storage-build src/main/java/*.java test/StorageTest.java
java -cp /private/tmp/friday-storage-build StorageTest
```

## Test Case: Add and list todos
- Aim: Verify that `todo` adds ToDo tasks and that `list` shows the ToDo prefix.
- Inputs:
```text
todo borrow book
todo buy bread
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy bread
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
2.[T][ ] buy bread
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid todo format
- Aim: Verify that `todo` shows an error message when the description is missing.
- Inputs:
```text
todo
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, description of a todo cannot be empty.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Unknown command
- Aim: Verify that unrecognized input produces a clear error message.
- Inputs:
```text
blah
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, I don't know what you are saying :-(
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Delete a task
- Aim: Verify that `delete` removes the selected task and shifts the remaining tasks up.
- Inputs:
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
delete 3
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add and list deadlines
- Aim: Verify that `deadline` adds Deadline tasks and shows the by-date.
- Inputs:
```text
deadline return book /by Sunday
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Sunday)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid deadline format
- Aim: Verify that `deadline` shows an error message when `/by` is missing.
- Inputs:
```text
deadline return book
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add and list events
- Aim: Verify that `event` adds Event tasks and shows the from/to times.
- Inputs:
```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid event format
- Aim: Verify that `event` shows an error message when `/from` or `/to` is missing.
- Inputs:
```text
event project meeting /from Mon 2pm
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid event format. Use: event DESCRIPTION /from START /to END
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Mark and unmark a todo
- Aim: Verify that `mark` and `unmark` update the done status of a ToDo.
- Inputs:
```text
todo borrow book
mark 1
unmark 1
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] borrow book
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] borrow book
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] borrow book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid mark format
- Aim: Verify that `mark` shows an error message when no task number is provided.
- Inputs:
```text
mark
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, Invalid mark format. Use: mark TASK_NUMBER
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid mark number
- Aim: Verify that `mark` shows an error message when the task number is out of range.
- Inputs:
```text
todo borrow book
mark 2
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid delete format
- Aim: Verify that `delete` shows an error message when no task number is provided.
- Inputs:
```text
delete
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, Invalid delete format. Use: delete TASK_NUMBER
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Unmarking an already unmarked task
- Aim: Verify that `unmark` reports when the task is already not done.
- Inputs:
```text
todo borrow book
unmark 1
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
This task is already not marked as done.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid unmark format
- Aim: Verify that `unmark` shows an error message when no task number is provided.
- Inputs:
```text
unmark
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sir, Invalid unmark format. Use: unmark TASK_NUMBER
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid unmark number
- Aim: Verify that `unmark` shows an error message when the task number is out of range.
- Inputs:
```text
todo borrow book
unmark 2
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Invalid delete number
- Aim: Verify that `delete` shows an error message when the task number is out of range.
- Inputs:
```text
todo borrow book
delete 2
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Sir, The task number is invalid.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Help command
- Aim: Verify that `help` prints the project link.
- Inputs:
```text
help
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Sure. Here you go:
https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Autosave all changes across restarts
- Aim: Verify addition, mark, unmark, deletion, and deletion of the last task persist across restarts without bye.
- Inputs:
```text
todo read book
deadline return book /by Sunday
event meeting /from Mon 2pm /to 4pm
mark 2
@restart
list
unmark 2
delete 1
@restart
list
delete 2
delete 1
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] read book
2.[D][X] return book (by: Sunday)
3.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Sunday)
____________________________________________________________
Noted. I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[D][ ] return book (by: Sunday)
2.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Noted. I've removed this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Sunday)
Now you have 0 tasks in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Load an existing save file
- Aim: Verify that all three task types and their completed statuses load from disk in order.
- Inputs:
```text
@file T|1|read book
@file D|1|return book|Sunday
@file E|1|meeting|Mon 2pm|4pm
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][X] read book
2.[D][X] return book (by: Sunday)
3.[E][X] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Preserve special characters
- Aim: Verify Unicode, pipes, and backslashes survive a save and restart unchanged.
- Inputs:
```text
todo café | C:\notes
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] café | C:\notes
Now you have 1 task in the list.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] café | C:\notes
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Protect a corrupted save file
- Aim: Verify malformed data produces a warning, no partial list is loaded, and session edits do not overwrite the original file.
- Inputs:
```text
@file T|0|valid first record
@file broken record
list
todo session only
@restart
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Got it. I've added this task:
  [T][ ] session only
Now you have 1 task in the list.
Warning: This change is only in memory; saving is disabled until you fix the file and restart.
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Unreadable save path
- Aim: Verify a directory at the save path produces a load warning without crashing.
- Inputs:
```text
@directory
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
Warning: I couldn't load data/friday.txt. Check the file and restart; saving is disabled to protect existing data.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Save failure keeps the chatbot usable
- Aim: Verify a failed save warns that changes are only in memory and the session list still works.
- Inputs:
```text
@block-save
todo session only
list
bye
```
- Expected output:
```text
____________________________________________________________
Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] session only
Now you have 1 task in the list.
Warning: I couldn't save data/friday.txt. Your changes are only in memory; check the folder and file permissions.
____________________________________________________________
Here are the tasks in your list:
Use the number shown here with mark/unmark.
1.[T][ ] session only
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
