package demo.project.action;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import demo.config.AwsLambdaConfig;
import demo.function.FunctionService;
import demo.function.model.LambdaResponse;
import demo.project.Project;
import demo.project.ProjectStatus;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED;

@Service
public class CreateProject {

    private final Logger log = Logger.getLogger(CreateProject.class);
    private final FunctionService functionService;

    public CreateProject(AwsLambdaConfig.FunctionInvoker functionService) {
        this.functionService = functionService.getFunctionService();
    }

    @HystrixCommand(fallbackMethod = "projectCreatedFallback", commandProperties = {
            @HystrixProperty(name = EXECUTION_TIMEOUT_ENABLED, value = "false")
    })
    public LambdaResponse<Project> apply(Map eventMap) {
        try {
            return new LambdaResponse<>(functionService.projectCreated(eventMap));
        } catch (Exception ex) {
            if (Objects.equals(ex.getMessage(), "Project already created")) {
                return new LambdaResponse<>(ex, null);
            } else {
                log.error("Error invoking AWS Lambda function", ex);
                throw ex;
            }
        }
    }

    public LambdaResponse<Project> projectCreatedFallback(Map eventMap) {
        Project project = (Project) eventMap.get("project");
        project.setStatus(ProjectStatus.PROJECT_CREATED);
        return new LambdaResponse<>(null, project);
    }
}
