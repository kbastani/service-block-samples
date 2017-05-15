package demo.messaging;

import demo.project.query.ProjectQueries;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventType;
import org.apache.log4j.Logger;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@EnableBinding(Sink.class)
public class EventProcessor {

    private final Logger log = Logger.getLogger(this.getClass());
    private final ProjectQueries projectQueries;

    public EventProcessor(ProjectQueries projectQueries) {
        this.projectQueries = projectQueries;
    }

    @StreamListener(value = Sink.INPUT)
    public void handle(Message<ProjectEvent> message) {

        ProjectEvent projectEvent = message.getPayload();

        log.info(projectEvent);

        // Invoke Lambda functions subscribing to project events
        // TODO: Create a registry that maps event types to Lambda functions
        if(projectEvent.getType() == ProjectEventType.COMMIT_EVENT) {
            projectQueries.getTightCoupling().apply(projectEvent);
        }
    }
}
