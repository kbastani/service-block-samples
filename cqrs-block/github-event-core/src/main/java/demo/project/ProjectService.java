package demo.project;

import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventRepository;
import demo.project.event.ProjectEventService;
import demo.project.event.ProjectEventType;
import demo.project.repository.CommitRepository;
import demo.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Map;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectEventRepository projectEventRepository;
    private final CommitRepository commitRepository;
    private final ProjectEventService eventService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectEventRepository projectEventRepository,
                          CommitRepository commitRepository, ProjectEventService eventService) {
        this.projectRepository = projectRepository;
        this.projectEventRepository = projectEventRepository;
        this.commitRepository = commitRepository;
        this.eventService = eventService;
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId).get();
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public ProjectEvent addProjectEvent(Long projectId, ProjectEvent projectEvent) {
        Project project = getProject(projectId);
        Assert.notNull(project, "Project could not be found");

        projectEvent.setProjectId(projectId);
        projectEvent.setEntity(project);
        projectEvent = projectEventRepository.saveAndFlush(projectEvent);
        project.getEvents().add(projectEvent);
        updateProject(project);

        return projectEvent;
    }

    public Commit addRepositoryCommit(Long projectId, Commit commit, Map<String, Object> payload) {
        Project project = getProject(projectId);
        Assert.notNull(project, "Project could not be found");

        commit.setProjectId(projectId);
        commit = commitRepository.saveAndFlush(commit);
        project.getCommits().add(commit);
        project = updateProject(project);

        ProjectEvent projectEvent = new ProjectEvent(ProjectEventType.COMMIT_EVENT, project, payload);
        eventService.apply(projectEvent, this);

        return commit;
    }
}
