package demo.event.messaging;

import demo.project.event.ProjectEvent;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class CommandStream {

    private final Source source;

    public CommandStream(Source source) {
        this.source = source;
    }

    public void handle(ProjectEvent projectEvent) {
        source.output().send(MessageBuilder.withPayload(projectEvent).build());
    }
}
