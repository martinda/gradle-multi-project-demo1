# A study on multi-project build

The goal of this repository is to study how multi-projet builds work.

The project applies a `buildSrc` plugin to sub-projects.

One would expect the plugin tasks to be available only in the subprojects,
but Gradle makes them available at the root project as well.

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

When asking for the list of tasks, gradle says the `MyPlugin` tasks are available to the root project:

```
$ ./gradlew tasks
...
MyTasks tasks
-------------
myTask - Print a message
...
```

Is this the expected behavior?
