import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        Task myTask1 = project.tasks.create('myTask1',MyTask)
        myTask1.group = 'MyTasks'
        myTask1.description = 'Print a message'
        Task myTask2 = project.tasks.create('myTask2',MyTask)
    }
}
