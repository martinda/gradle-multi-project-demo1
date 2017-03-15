# A study on multi-project build

The goal of this repository is to study how multi-projet builds work.

The project applies a `buildSrc` plugin called `MyPlugin` to
subprojects. This plugin adds two custom tasks to the project: `myTask1`
and `myTask2`.

One may expect the plugin tasks to be available only in the subprojects,
but Gradle makes them available at the root project as well. However, this
is not always made clear when asking gradle for the list of tasks.

Note that the plugin set the `group` and `description` properties of
`myTask1`, but not of `myTask2`. This changes the visibility of the tasks
when asking gradle for the list of tasks, but does not change the behavior.

# Example

The `build.gradle` contains this code:

```
plugins {
    id 'base'
}

def onlySubProjects = subprojects.findAll {
    it.path.contains(':exp1:')
}

configure(onlySubProjects) {
    apply plugin: MyPlugin
}
```

When asking for the list of tasks, gradle says that `myTask1` is
available to the root project:

```
$ ./gradlew tasks
...
MyTasks tasks
-------------
myTask1 - Print a message
...
```

A user may wonder why `myTask1` is accessible from the rootProject
when ostensibly it has only been applied to the subproject. It turns
out this is a pretty useful feature: from the root project, you can
execute subproject tasks, without having to specify the full path to
the subproject task itself.

When asking gradle for the list of all tasks, gradle says the subprojects
have the following tasks, but does not list that the root project still
has them too:

```
$ ./gradlew tasks --all
...
MyTasks tasks
-------------
exp1:one:myTask1 - Print a message
exp1:two:myTask1 - Print a message
...
Other tasks
-----------
exp1:one:myTask2
exp1:two:myTask2
...

```

Yes, Gradle now tells you that those tasks are really subproject tasks,
but it no longer tells you that the same tasks as also callable from
the root project. To know what's available from the root project, you
need to run `./gradlew tasks`.

When running the tasks from the root project, gradle dives into the
subprojects and runs the tasks:

```
$ ./gradlew myTask1
...
:exp1:one:myTask1
Message from task: task1 one
:exp1:two:myTask1
Message from task: task1 two

BUILD SUCCESSFUL

Total time: 0.664 secs
```

And the same happens when calling `myTask2` (despite it not being listed
in as a root project task):

```
:exp1:one:myTask2
Message from task: task2 one
:exp1:two:myTask2
Message from task: task2 two

BUILD SUCCESSFUL

Total time: 0.65 secs
```

Task visibility can be confusing to the user as shown above. To summarize:

* A task that was created in the subproject only, can be run from the root project.
* A subproject task that is not listed by `gradle tasks`, can be run from the root project (e.g. `myTask2`).

