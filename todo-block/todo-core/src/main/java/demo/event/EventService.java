package demo.event;

import demo.command.ProjectCommands;
import demo.project.Project;
import demo.project.ProjectService;
import demo.project.event.ProjectEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Map;

@Service
public class EventService {

    final private Logger log = Logger.getLogger(EventService.class);
    final private ProjectCommands projectActions;

    public EventService(ProjectCommands projectActions) {
        this.projectActions = projectActions;
    }

    @Transactional
    public ProjectEvent apply(ProjectEvent projectEvent, ProjectService projectService) {
        Project project = projectService.getProject(projectEvent.getProjectId());

        // Cache payload before handling the event
        Map<String, Object> payload = projectEvent.getPayload();

        if (project == null)
            project = projectEvent.getEntity();

        Assert.notNull(project, "A project for that ID does not exist");

        // Map the event type to the corresponding command handler
        switch (projectEvent.getType()) {
            case CREATED_EVENT:
                project = projectActions.getCreateProject().apply(projectEvent);
                break;
            case COMMIT_EVENT:
                project = projectActions.getCommitProject().apply(projectEvent);
                break;
        }

        // Apply command updates
        project = projectService.updateProject(project);

        // Add the event and reset the transient payload
        projectEvent = projectService.addProjectEvent(project.getIdentity(), projectEvent);
        projectEvent.setPayload(payload);
        projectEvent.setEntity(project);

        return projectEvent;
    }
}
