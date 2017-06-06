package demo.messaging;

import demo.event.EventService;
import demo.project.Commit;
import demo.project.File;
import demo.project.Project;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventType;
import demo.project.query.ProjectQueries;
import demo.project.repository.CommitRepository;
import demo.project.repository.ProjectRepository;
import github.GitHubTemplate;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableBinding(Sink.class)
public class EventProcessor {

    private final Logger log = Logger.getLogger(this.getClass());
    private final ProjectQueries projectQueries;
    private final GitHubTemplate gitTemplate;
    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private final EventService eventService;

    public EventProcessor(ProjectQueries projectQueries, GitHubTemplate gitTemplate, ProjectRepository projectRepository, CommitRepository commitRepository, EventService eventService) {
        this.projectQueries = projectQueries;
        this.gitTemplate = gitTemplate;
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.eventService = eventService;
    }

    @Transactional
    @StreamListener(value = Sink.INPUT)
    public void handle(Message<ProjectEvent> message) {

        ProjectEvent projectEvent = message.getPayload();
        Project project = projectRepository.findOne(projectEvent.getProjectId());

        log.info(projectEvent);

        if (projectEvent.getType() == ProjectEventType.CREATED_EVENT) {
            try {
                importProjectCommits(projectEvent, project);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Invoke Lambda functions subscribing to project events
        // TODO: Create a registry that maps event types to Lambda functions
        if (projectEvent.getType() == ProjectEventType.COMMIT_EVENT) {
            projectQueries.getTightCoupling().apply(projectEvent);
        }
    }

    private void importProjectCommits(ProjectEvent projectEvent, Project project) throws IOException {
        // Import the new project
        RepositoryId repositoryId = new RepositoryId(project.getOwner(), project.getName());
        List<RepositoryCommit> repositoryCommits = gitTemplate.commitService().getCommits(repositoryId);

        List<Commit> commits;
        commits = repositoryCommits.stream()
                .map(c -> {
                    try {
                        return gitTemplate.commitService()
                                .getCommit(repositoryId, c.getSha());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(a -> a.getFiles() != null)
                .map(a -> new Commit(a.getFiles().stream()
                        .map(f -> new File(f.getFilename()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        final Project updateProject =  projectRepository.findOne(project.getIdentity());
        commits.forEach(commit -> {
            commit.setProjectId(updateProject.getIdentity());
            commit = commitRepository.save(commit);
            updateProject.getCommits().add(commit);
            projectRepository.save(updateProject);

            Map<String, Object> payload = new HashMap<>();
            payload.put("commit", commit);

            // Generate commit event
            eventService.apply(new ProjectEvent(ProjectEventType.COMMIT_EVENT, updateProject, payload));
        });
    }
}
