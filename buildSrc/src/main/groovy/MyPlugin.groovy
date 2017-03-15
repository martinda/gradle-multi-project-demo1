import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        Task myTask = project.tasks.create('myTask',MyTask)
        myTask.group = 'MyTasks'
        myTask.description = 'Print a message'
    }
}
