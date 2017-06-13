package demo.project.event;

import demo.event.EventService;
import demo.project.ProjectService;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectEventService {

    private final EventService eventService;
    private final Source source;

    public ProjectEventService(EventService eventService, Source source) {
        this.eventService = eventService;
        this.source = source;
    }

    @Transactional
    public ProjectEvent apply(ProjectEvent projectEvent, ProjectService projectService) {
        projectEvent = eventService.apply(projectEvent, projectService);

        // Send the event to the event stream to update query models
        sendEvent(projectEvent);

        return projectEvent;
    }

    private void sendEvent(ProjectEvent projectEvent) {
        source.output().send(MessageBuilder.withPayload(projectEvent).build());
    }
}
