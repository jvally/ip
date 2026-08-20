# UI Test Plan

This file is the source of truth for command-driven UI regression tests.
Update it whenever the console UI changes.

Program command: `javac -d /private/tmp/ui-test-build src/main/java/*.java && java -cp /private/tmp/ui-test-build Friday`

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
1.[T][ ] borrow book
2.[T][ ] buy bread
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
1.[D][ ] return book (by: Sunday)
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
1.[T][ ] borrow book
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
