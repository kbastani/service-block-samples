package demo.event;

import demo.project.action.ProjectActions;
import demo.function.model.LambdaResponse;
import demo.project.Project;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventRepository;
import demo.project.repository.ProjectRepository;
import org.apache.log4j.Logger;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventService {

    final private Logger log = Logger.getLogger(EventService.class);
    final private ProjectRepository projectRepository;
    final private ProjectEventRepository projectEventRepository;
    final private ProjectActions projectActions;
    final private Source source;

    public EventService(ProjectRepository projectRepository,
                        ProjectEventRepository projectEventRepository,
                        ProjectActions projectActions, Source source) {
        this.projectRepository = projectRepository;
        this.projectEventRepository = projectEventRepository;
        this.projectActions = projectActions;
        this.source = source;
    }

    public Project apply(ProjectEvent projectEvent) {

        Project project = projectRepository.findOne(projectEvent.getProjectId());

        // Cache payload before handling the event
        Map<String, Object> payload = projectEvent.getPayload();

        if (project == null)
            project = projectEvent.getEntity();

        Assert.notNull(project, "A project for that ID does not exist");

        // Get a history of events for this project
        List<ProjectEvent> events = projectEventRepository
                .findEventsByProjectId(projectEvent.getProjectId());

        // Sort the events reverse chronological
        events.sort(Comparator.comparing(ProjectEvent::getCreatedAt).reversed());

        LambdaResponse<Project> result = null;

        // Map the event type to the corresponding command handler
        switch (projectEvent.getType()) {
            case CREATED_EVENT:
                result = projectActions.getCreateProject()
                        .apply(getProjectEventMap(projectEvent, events, project));
                break;
            case COMMIT_EVENT:
                result = projectActions.getCommitProject()
                        .apply(getProjectEventMap(projectEvent, events, project));
                break;
        }

        // A response from Lambda was returned
        if (result != null) {
            if (result.getException() != null) {
                throw new RuntimeException(result.getException().getMessage(),
                        result.getException());
            }

            Assert.notNull(result.getPayload(), "Lambda response payload must not be null");
            project.setStatus(result.getPayload().getStatus());
            projectEvent.setEntity(result.getPayload());

            log.info(result.getPayload());
        }

        // Add the event and save the new project status
        addEvent(projectEvent, project);
        project = projectRepository.save(project);
        projectRepository.flush();

        // Send event to the command stream for query handlers to build materialized views
        projectEvent.setPayload(payload);
        projectEvent.setProjectId(project.getIdentity());
        send(projectEvent);

        return project;
    }

    private ProjectEvent addEvent(ProjectEvent projectEvent, Project project) {
        projectEvent = projectEventRepository.save(projectEvent);
        project.getEvents().add(projectEvent);
        return projectEvent;
    }

    private Map<String, Object> getProjectEventMap(ProjectEvent event, List<ProjectEvent> events, Project project) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("projectEvent", event);
        eventMap.put("eventLog", events);
        eventMap.put("project", project);
        return eventMap;
    }

    public void send(ProjectEvent projectEvent) {
        source.output().send(MessageBuilder.withPayload(projectEvent).build());
    }
}
