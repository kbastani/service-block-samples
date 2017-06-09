package demo.functions;

import demo.functions.project.Project;
import demo.functions.project.ProjectEventParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class ProjectCommittedFunction {

    public static void main(String[] args) {
        SpringApplication.run(ProjectCommittedFunction.class, args);
    }

    @Bean
    public Function<ProjectEventParam, Project> function() {
        return projectEventParam -> {
            Project project = projectEventParam.getProject();
            return project;
        };
    }
}
