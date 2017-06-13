package demo.query;

import demo.command.CreateProject;
import demo.config.AwsLambdaConfig;
import demo.function.FunctionService;
import demo.function.LambdaResponse;
import demo.project.Project;
import demo.project.event.ProjectEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TightCoupling {

    private final Logger log = Logger.getLogger(CreateProject.class);
    private final FunctionService functionService;
    private final QueryEventRepository eventRepository;

    public TightCoupling(AwsLambdaConfig.FunctionInvoker functionService,
                         QueryEventRepository eventRepository) {
        this.functionService = functionService.getFunctionService();
        this.eventRepository = eventRepository;
    }

    public LambdaResponse<Map<String, Object>> apply(ProjectEvent event) {
        try {
            Map<String, Object> result =
                    functionService.tightCouplingQuery(getProjectEventMap(event, event.getEntity()));

            // Check for new tight coupling events
            List<TightCouplingEvent> events = (List<TightCouplingEvent>)result.get("events");

            // Save the new events
            eventRepository.saveAll(Flux.fromStream(events.stream()));

            return new LambdaResponse<>(result);
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
