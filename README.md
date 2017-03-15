# A study on multi-project build

The goal of this repository is to study how multi-projet builds work.

The project applies a `buildSrc` plugin to sub-projects.

One would expect the plugin tasks to be available only in the subprojects,
but Gradle makes them available at the root project as well.
