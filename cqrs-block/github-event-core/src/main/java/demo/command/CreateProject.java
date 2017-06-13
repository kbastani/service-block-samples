package demo.command;

import demo.project.Project;
import demo.project.ProjectStatus;
import demo.project.event.ProjectEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class CreateProject {

    private final Logger log = Logger.getLogger(CreateProject.class);

    public Project apply(ProjectEvent projectEvent) {
        log.info("New project: " + projectEvent);
        Assert.isTrue((projectEvent.getEntity().getStatus() == ProjectStatus.PROJECT_CREATED ||
                        projectEvent.getEntity().getStatus() == null), "Project is in an invalid state");
        Project project = projectEvent.getEntity();
        project.setStatus(ProjectStatus.PROJECT_CREATED);
        return project;
    }
}
