package demo.query;

import demo.command.CreateProject;
import demo.config.AwsLambdaConfig;
import demo.function.FunctionService;
import demo.function.LambdaResponse;
import demo.project.Project;
import demo.project.event.ProjectEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TightCoupling {

    private final Logger log = Logger.getLogger(CreateProject.class);
    private final FunctionService functionService;

    public TightCoupling(AwsLambdaConfig.FunctionInvoker functionService) {
        this.functionService = functionService.getFunctionService();
    }

    public LambdaResponse<Map<String, Object>> apply(ProjectEvent event) {
        try {
            return new LambdaResponse<>(functionService.tightCouplingQuery(getProjectEventMap(event, event.getEntity())));
        } catch (Exception ex) {
            log.error("Error invoking AWS Lambda function", ex);
            throw ex;
        }
    }

    private Map<String, Object> getProjectEventMap(ProjectEvent event, Project project) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("projectEvent", event);
        eventMap.put("project", new Project(event.getProjectId()));
        return eventMap;
    }
}
