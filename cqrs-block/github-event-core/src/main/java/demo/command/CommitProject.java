package demo.command;

import demo.project.Project;
import demo.project.ProjectStatus;
import demo.project.event.ProjectEvent;
import demo.project.repository.CommitRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class CommitProject {

    private final Logger log = Logger.getLogger(CommitProject.class);
    private final CommitRepository commitRepository;

    public CommitProject(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    public Project apply(ProjectEvent projectEvent) {
        log.info("New commit: " + projectEvent);
        Assert.isTrue(projectEvent.getEntity().getStatus() == ProjectStatus.PROJECT_CREATED,
                "Project is in an invalid state");
        Project project = projectEvent.getEntity();
        return project;
    }
}
