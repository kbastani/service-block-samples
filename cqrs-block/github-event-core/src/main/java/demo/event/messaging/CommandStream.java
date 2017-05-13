package demo.event.messaging;

import demo.project.event.ProjectEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(Source.class)
public class CommandStream {

    @Output(Source.OUTPUT)
    public ProjectEvent handle(ProjectEvent event) {
        return event;
    }
}
