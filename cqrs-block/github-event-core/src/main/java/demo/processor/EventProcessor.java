package demo.processor;

import demo.function.LambdaResponse;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventType;
import demo.query.ProjectQueries;
import org.apache.log4j.Logger;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.Map;

@Configuration
@EnableBinding(Sink.class)
public class EventProcessor {

    private final Logger log = Logger.getLogger(this.getClass());
    private final ProjectQueries projectQueries;
    private final CommitProcessor commitProcessor;

    public EventProcessor(ProjectQueries projectQueries, CommitProcessor commitProcessor) {
        this.projectQueries = projectQueries;
        this.commitProcessor = commitProcessor;
    }

    @StreamListener(value = Sink.INPUT)
    public void handle(Message<ProjectEvent> message) {
        ProjectEvent projectEvent = message.getPayload();
        log.info("Received new event: " + "{ projectId " + projectEvent.getProjectId() + " -> " +
                projectEvent.getType() + " }");

        if (projectEvent.getType() == ProjectEventType.CREATED_EVENT) {
            try {
                commitProcessor.importCommits(projectEvent);
            } catch (IOException e) {
                throw new RuntimeException("Could not import GitHub project", e);
            }
        }

        if (projectEvent.getType() == ProjectEventType.COMMIT_EVENT) {
            // Update query models
            LambdaResponse<Map<String, Object>> response =
                    projectQueries.getTightCoupling().apply(projectEvent);
        }
    }
}
