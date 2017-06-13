package demo.command;

import org.springframework.stereotype.Service;

@Service
public class ProjectCommands {

    private final CreateProject createProject;
    private final CommitProject commitProject;

    public ProjectCommands(CreateProject createProject, CommitProject commitProject) {
        this.createProject = createProject;
        this.commitProject = commitProject;
    }

    public CreateProject getCreateProject() {
        return createProject;
    }

    public CommitProject getCommitProject() {
        return commitProject;
    }
}
