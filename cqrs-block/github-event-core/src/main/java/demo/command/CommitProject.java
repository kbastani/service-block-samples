package demo.command;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import demo.config.AwsLambdaConfig;
import demo.function.FunctionService;
import demo.model.LambdaResponse;
import demo.project.Project;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED;

@Service
public class CommitProject {

    private final Logger log = Logger.getLogger(CommitProject.class);
    private final FunctionService functionService;

    public CommitProject(AwsLambdaConfig.FunctionInvoker functionService) {
        this.functionService = functionService.getFunctionService();
    }

    @HystrixCommand(fallbackMethod = "projectCommittedFallback", commandProperties = {
            @HystrixProperty(name = EXECUTION_TIMEOUT_ENABLED, value = "false")
    })
    public LambdaResponse<Project> apply(Map eventMap) {
        try {
            return new LambdaResponse<>(functionService.projectCommitted(eventMap));
        } catch (Exception ex) {
            if (Objects.equals(ex.getMessage(), "Project is in an invalid state")) {
                return new LambdaResponse<>(ex, null);
            } else {
                log.error("Error invoking AWS Lambda function", ex);
                throw ex;
            }
        }
    }

    public LambdaResponse<Project> projectCommittedFallback(Map eventMap) {
        Project project = (Project) eventMap.get("project");
        return new LambdaResponse<>(null, project);
    }
}
