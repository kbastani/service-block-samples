package demo.project.action;

import org.springframework.stereotype.Service;

@Service
public class ProjectActions {

    private final CreateProject createProject;
    private final CommitProject commitProject;

    public ProjectActions(CreateProject createProject, CommitProject commitProject) {
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
