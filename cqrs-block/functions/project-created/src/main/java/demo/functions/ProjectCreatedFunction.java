package demo.functions;

import demo.functions.project.Project;
import demo.functions.project.ProjectEventParam;
import demo.functions.project.ProjectStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class ProjectCreatedFunction {

    public static void main(String[] args) {
        SpringApplication.run(ProjectCreatedFunction.class, args);
    }

    @Bean
    public Function<ProjectEventParam, Project> function() {
        return projectEventParam -> {
            Project project = projectEventParam.getProject();
            project.setStatus(ProjectStatus.PROJECT_CREATED);
            return project;
        };
    }
}
