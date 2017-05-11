package demo.event.messaging;

import demo.function.QueryHandlers;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventType;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

@EnableBinding(Sink.class)
public class QueryStream {

    private final QueryHandlers queryHandlers;

    public QueryStream(QueryHandlers queryHandlers) {
        this.queryHandlers = queryHandlers;
    }

    @StreamListener(value = Sink.INPUT)
    public void handle(Message<ProjectEvent> message) {

        ProjectEvent projectEvent = message.getPayload();

        // Invoke Lambda functions subscribing to project events
        // TODO: Create a registry that maps event types to Lambda functions
        if(projectEvent.getType() == ProjectEventType.COMMIT_EVENT) {
            queryHandlers.getTightCouplingQuery().apply(projectEvent);
        }
    }
}
