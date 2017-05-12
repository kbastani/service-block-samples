package demo.function;

import demo.command.CommitProject;
import demo.command.CreateProject;
import org.springframework.stereotype.Service;

@Service
public class CommandHandlers {

    private final CreateProject createProject;
    private final CommitProject commitProject;

    public CommandHandlers(CreateProject createProject, CommitProject commitProject) {
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
