package demo.event;

import demo.event.messaging.CommandStream;
import demo.function.CommandHandlers;
import demo.model.LambdaResponse;
import demo.project.Project;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventRepository;
import demo.project.repository.ProjectRepository;
import org.apache.log4j.Logger;
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
    final private CommandHandlers commandHandlers;
    final private CommandStream commandStream;

    public EventService(ProjectRepository projectRepository,
                        ProjectEventRepository projectEventRepository,
                        CommandHandlers commandHandlers, CommandStream commandStream) {
        this.projectRepository = projectRepository;
        this.projectEventRepository = projectEventRepository;
        this.commandHandlers = commandHandlers;
        this.commandStream = commandStream;
    }

    public Project apply(ProjectEvent projectEvent) {

        Project project = projectRepository.findOne(projectEvent.getProjectId());

        if (project == null)
            project = projectEvent.getEntity();

        Assert.notNull(project, "A project for that ID does not exist");

        // Get a history of events for this project
        List<ProjectEvent> events = projectEventRepository
                .findEventsByProjectId(projectEvent.getProjectId());

        // Sort the events reverse chronological
        events.sort(Comparator.comparing(ProjectEvent::getCreatedAt).reversed());

        LambdaResponse<Project> result = null;

        // Route requests to serverless functions
        switch (projectEvent.getType()) {
            case CREATED_EVENT:
                result = commandHandlers.getCreateProject()
                        .apply(getProjectEventMap(projectEvent, events, project));
                break;
            case COMMIT_EVENT:
                result = commandHandlers.getCommitProject()
                        .apply(getProjectEventMap(projectEvent, events, project));
                break;
        }

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

        commandStream.handle(projectEvent);

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
}
