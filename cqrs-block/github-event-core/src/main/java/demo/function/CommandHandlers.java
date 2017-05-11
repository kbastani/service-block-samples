package demo.function;

import demo.command.CreateProject;
import org.springframework.stereotype.Service;

@Service
public class CommandHandlers {

    private final CreateProject createProject;

    public CommandHandlers(CreateProject createProject) {
        this.createProject = createProject;
    }

    public CreateProject getCreateProject() {
        return createProject;
    }
}
