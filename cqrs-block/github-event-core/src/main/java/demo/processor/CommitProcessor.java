package demo.processor;

import demo.project.Commit;
import demo.project.File;
import demo.project.Project;
import demo.project.ProjectService;
import demo.project.event.ProjectEvent;
import github.GitHubTemplate;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommitProcessor {

    private final Logger log = Logger.getLogger(this.getClass());
    private final GitHubTemplate gitTemplate;
    private final ProjectService projectService;

    public CommitProcessor(GitHubTemplate gitTemplate, ProjectService projectService) {
        this.projectService = projectService;
        this.gitTemplate = gitTemplate;
    }

    public void importCommits(ProjectEvent projectEvent) throws IOException {
        Project project = projectService.getProject(projectEvent.getProjectId());
        Assert.notNull(project, "Could not find project with the provided id");

        // Get GitHub repository commits
        RepositoryId repositoryId = new RepositoryId(project.getOwner(), project.getName());
        List<RepositoryCommit> repositoryCommits = gitTemplate.commitService().getCommits(repositoryId);

        Flux<Commit> commits;

        commits = Flux.fromStream(repositoryCommits.stream()).map(c -> {
            try {
                log.info("Importing commit: " + repositoryId.getName() + " -> " + c.getSha());
                return gitTemplate.commitService().getCommit(repositoryId, c.getSha());
            } catch (IOException e) {
                throw new RuntimeException("Could not get commit", e);
            }
        }).filter(a -> a.getFiles() != null && a.getFiles().size() < 10)
                .map(a -> new Commit(a.getFiles().stream()
                        .map(f -> new File(f.getFilename())).collect(Collectors.toList())));

        commits.subscribe(commit -> saveCommits(project, commit));
    }

    private void saveCommits(Project project, Commit commit) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("commit", commit);
        projectService.addRepositoryCommit(project.getIdentity(), commit, payload);
    }
}
