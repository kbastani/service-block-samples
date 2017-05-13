package demo.event.messaging;

import demo.function.QueryHandlers;
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
public class QueryStream {

    private final Logger log = Logger.getLogger(this.getClass());
    private final QueryHandlers queryHandlers;

    public QueryStream(QueryHandlers queryHandlers) {
        this.queryHandlers = queryHandlers;
    }

    @StreamListener(value = Sink.INPUT)
    public void handle(Message<ProjectEvent> message) {

        ProjectEvent projectEvent = message.getPayload();

        log.info(projectEvent);

        // Invoke Lambda functions subscribing to project events
        // TODO: Create a registry that maps event types to Lambda functions
        if(projectEvent.getType() == ProjectEventType.COMMIT_EVENT) {
            queryHandlers.getTightCouplingQuery().apply(projectEvent);
        }
    }
}
