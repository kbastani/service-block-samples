package demo.event;

import demo.project.ProjectService;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class EventController {

    private final ProjectService projectService;
    private final ProjectEventService projectEventService;

    public EventController(ProjectService projectService, ProjectEventService projectEventService) {
        this.projectService = projectService;
        this.projectEventService = projectEventService;
    }

    @PostMapping(path = "/events")
    public ResponseEntity handleEvent(@RequestBody ProjectEvent event) {
        return Optional.ofNullable(projectEventService.apply(event, projectService))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Apply event failed"));
    }
}
