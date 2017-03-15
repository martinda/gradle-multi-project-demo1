# A study on tasks visibility in multi-project builds

The goal of this repository is to study the visibility of tasks
in multi-project builds.

The project in this study applies a `buildSrc` plugin called `MyPlugin`
to subprojects (and subprojects only). It adds two custom tasks: `myTask1`
and `myTask2`.  The plugin is not applied to the root project.

One may expect the plugin tasks to be available only in the subprojects,
but Gradle makes them callable from the root project as well. It is
important to understand how Gradle behaves to reduce the confusion around
tasks in multi-project builds.

Note that the plugin sets the `group` and `description` properties of
`myTask1`, but not of `myTask2`. This changes the visibility of the tasks
when asking gradle for the list of tasks, but does not change the behavior.

# Theory

There are three different independent aspects to tasks from the user
point of view.

* *configurability*: A task can only be configured in the project where it has been applied.
* *visibility*: A task is visible when running `./gradlew tasks` when its `group` and `description` have been set. It is also visible when typing `./gradlew tasks --all` regardless of `group` and `description`.
* *callability*: A task can be called from its subproject or from the root project, whether it is visible or not.

# Example

The `build.gradle` contains the following code:

```
plugins {
    id 'base'
}

// Create a list of selected subprojects
def onlySubProjects = subprojects.findAll {
    it.path.contains(':exp1:')
}

// Apply the plugin only to the selected subprojects
configure(onlySubProjects) {
    apply plugin: MyPlugin
}
```

The plugin was applied only to the subprojects. If you try to configure
the plugin tasks in the root project, it will fail. In order words,
if you try to configure `myTask1` or `myTask2` from the root project,
it will not work:

```
myTask1 {
    msg = 'root project'
}
```

The above code in the root project `build.gradle` file will fail.

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

A user may wonder why `myTask1` is accessible from the rootProject when
it has only been applied to the subproject. It turns out this is a pretty
useful feature: from the root project, you can execute subproject tasks,
without having to specify the full path to the subproject.

When asking gradle for the list of all tasks, gradle says the subprojects
have the tasks, but does not list that the root project can still
call them:

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
the root project. To know what's callable from the root project, you
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
as a root project task):

```
:exp1:one:myTask2
Message from task: task2 one
:exp1:two:myTask2
Message from task: task2 two

BUILD SUCCESSFUL

Total time: 0.65 secs
```

Task visibility can be confusing to the user at first. To summarize:

* A task that was created in the subproject only, can be run from the root project.
* A subproject task that is not listed by `gradle tasks`, can also be run from the root project (e.g. `myTask2`).

