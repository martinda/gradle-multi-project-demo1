import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input

class MyTask extends DefaultTask {
    @Input
    String msg

    @TaskAction
    void run() {
        println('Message from task: '+msg)
    }
}
