# UI Test Plan

This file is the source of truth for command-driven UI regression tests.
Update it whenever the console UI changes.

Program command: `javac -d /private/tmp/ui-test-build src/main/java/*.java && java -cp /private/tmp/ui-test-build Friday`

## Test Case: Add and list tasks
- Aim: Verify that new tasks are stored and shown by `list`.
- Inputs:
```text
read book
return book
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
added: read book
____________________________________________________________
added: return book
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
2.[ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Mark and unmark a task
- Aim: Verify that `mark` and `unmark` update the done status.
- Inputs:
```text
read book
return book
mark 2
unmark 2
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
added: read book
____________________________________________________________
added: return book
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
Here are the tasks in your list:
1.[ ] read book
2.[ ] return book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Unmarking an already unmarked task
- Aim: Verify that `unmark` reports when the task is already not done.
- Inputs:
```text
read book
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
added: read book
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
