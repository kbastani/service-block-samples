package demo.project.controller;

import demo.event.EventService;
import demo.project.Commit;
import demo.project.Project;
import demo.project.event.ProjectEvent;
import demo.project.event.ProjectEventRepository;
import demo.project.event.ProjectEventType;
import demo.project.repository.CommitRepository;
import demo.project.repository.ProjectRepository;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/v1")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private final EventService eventService;
    private final ProjectEventRepository eventRepository;

    public ProjectController(ProjectRepository projectRepository, CommitRepository commitRepository, EventService eventService,
                             ProjectEventRepository eventRepository) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(path = "/projects")
    public ResponseEntity getProjects() {
        return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/projects")
    public ResponseEntity createProject(@RequestBody Project project) {
        return Optional.ofNullable(createProjectResource(project))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Project creation failed"));
    }

    @PutMapping(path = "/projects/{id}")
    public ResponseEntity updateProject(@RequestBody Project project, @PathVariable Long id) {
        return Optional.ofNullable(updateProjectResource(id, project))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Project update failed"));
    }

    @RequestMapping(path = "/projects/{id}")
    public ResponseEntity getProject(@PathVariable Long id) {
        return Optional.ofNullable(projectRepository.findOne(id))
                .map(this::getProjectResource)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/projects/{id}")
    public ResponseEntity deleteProject(@PathVariable Long id) {
        try {
            projectRepository.delete(id);
        } catch (Exception ex) {
            throw new RuntimeException("Project deletion failed");
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/projects/{id}/commits")
    public ResponseEntity getProjectCommits(@PathVariable Long id) {
        return Optional.of(getProjectCommitResources(id))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Could not get project commits"));
    }

    @PostMapping(path = "/projects/{id}/commits")
    public ResponseEntity appendProjectCommit(@PathVariable Long id, @RequestBody Commit commit) {
        return Optional.ofNullable(appendCommitResource(id, commit))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Append project commit failed"));
    }

    @RequestMapping(path = "/projects/{id}/events")
    public ResponseEntity getProjectEvents(@PathVariable Long id) {
        return Optional.of(getProjectEventResources(id))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Could not get project events"));
    }

    @RequestMapping(path = "/projects/{id}/events/{eventId}")
    public ResponseEntity getProjectEvent(@PathVariable Long id, @PathVariable Long eventId) {
        return Optional.of(getEventResource(eventId))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Could not get order events"));
    }

    @PostMapping(path = "/projects/{id}/events")
    public ResponseEntity appendProjectEvent(@PathVariable Long id, @RequestBody ProjectEvent event) {
        return Optional.ofNullable(appendEventResource(id, event))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Append project event failed"));
    }

    @RequestMapping(path = "/projects/{id}/commands")
    public ResponseEntity getCommands(@PathVariable Long id) {
        return Optional.ofNullable(getCommandsResource(id))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("The project could not be found"));
    }

    @PostMapping(path = "/projects/{id}/commands/commit")
    public ResponseEntity commitProject(@PathVariable Long id, @RequestBody Commit commit) {
        return Optional.ofNullable(appendCommitResource(id, commit))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Append project commit failed"));
    }

    /**
     * Creates a new {@link Project} entity and persists the result to the repository.
     *
     * @param project is the {@link Project} model used to create a new project
     * @return a hypermedia resource for the newly created {@link Project}
     */
    private Resource<Project> createProjectResource(Project project) {
        Assert.notNull(project, "Project body must not be null");

        // Create the new project
        project = projectRepository.save(project);

        project = eventService.apply(new ProjectEvent(ProjectEventType.CREATED_EVENT, project));

        return getProjectResource(project);
    }

    /**
     * Update a {@link Project} entity for the provided identifier.
     *
     * @param id      is the unique identifier for the {@link Project} update
     * @param project is the entity representation containing any updated {@link Project} fields
     * @return a hypermedia resource for the updated {@link Project}
     */
    private Resource<Project> updateProjectResource(Long id, Project project) {
        project.setIdentity(id);
        return getProjectResource(projectRepository.save(project));
    }

    /**
     * Appends an {@link Commit} domain commit to the commit log of the {@link Project}
     * aggregate with the specified projectId.
     *
     * @param projectId is the unique identifier for the {@link Project}
     * @param commit    is the {@link Commit} that attempts to alter the state of the {@link Project}
     * @return a hypermedia resource for the newly appended {@link Commit}
     */
    private Resource<Commit> appendCommitResource(Long projectId, Commit commit) {
        Assert.notNull(commit, "Commit body must be provided");

        Project project = projectRepository.findOne(projectId);
        Assert.notNull(project, "Project could not be found");

        commit.setProjectId(project.getIdentity());
        commit = commitRepository.save(commit);
        project.getCommits().add(commit);
        projectRepository.save(project);

        Map<String, Object> payload = new HashMap<>();
        payload.put("commit", commit);

        // Generate commit event
        eventService.apply(new ProjectEvent(ProjectEventType.COMMIT_EVENT, project, payload));

        return new Resource<>(commit,
                linkTo(CommitController.class)
                        .slash("commits")
                        .slash(commit.getIdentity())
                        .withSelfRel(),
                linkTo(ProjectController.class)
                        .slash("projects")
                        .slash(projectId)
                        .withRel("project")
        );
    }

    /**
     * Appends an {@link ProjectEvent} domain event to the event log of the {@link Project}
     * aggregate with the specified projectId.
     *
     * @param projectId is the unique identifier for the {@link Project}
     * @param event     is the {@link ProjectEvent} that attempts to alter the state of the {@link Project}
     * @return a hypermedia resource for the newly appended {@link ProjectEvent}
     */
    private Resource<ProjectEvent> appendEventResource(Long projectId, ProjectEvent event) {
        Assert.notNull(event, "Event body must be provided");

        Project project = projectRepository.findOne(projectId);
        Assert.notNull(project, "Project could not be found");

        event.setProjectId(project.getIdentity());
        eventService.apply(event);

        return new Resource<>(event,
                linkTo(ProjectController.class)
                        .slash("projects")
                        .slash(projectId)
                        .slash("events")
                        .slash(event.getEventId())
                        .withSelfRel(),
                linkTo(ProjectController.class)
                        .slash("projects")
                        .slash(projectId)
                        .withRel("project")
        );
    }

    private ProjectEvent getEventResource(Long eventId) {
        return eventRepository.findOne(eventId);
    }

    private List<ProjectEvent> getProjectEventResources(Long id) {
        return eventRepository.findEventsByProjectId(id);
    }

    private List<Commit> getProjectCommitResources(Long id) {
        return commitRepository.findCommitsByProjectId(id);
    }

    private LinkBuilder linkBuilder(String name, Long id) {
        Method method;

        try {
            method = ProjectController.class.getMethod(name, Long.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return linkTo(ProjectController.class, method, id);
    }

    /**
     * Get a hypermedia enriched {@link Project} entity.
     *
     * @param project is the {@link Project} to enrich with hypermedia links
     * @return is a hypermedia enriched resource for the supplied {@link Project} entity
     */
    private Resource<Project> getProjectResource(Project project) {
        Assert.notNull(project, "Project must not be null");

        if (!project.hasLink("commands")) {
            // Add command link
            project.add(linkBuilder("getCommands", project.getIdentity()).withRel("commands"));
        }

        if (!project.hasLink("events")) {
            // Add get events link
            project.add(linkBuilder("getProjectEvents", project.getIdentity()).withRel("events"));
        }

        if (!project.hasLink("commits")) {
            // Add get events link
            project.add(linkBuilder("getProjectCommits", project.getIdentity()).withRel("commits"));
        }

        return new Resource<>(project);
    }

    private ResourceSupport getCommandsResource(Long id) {
        Project project = new Project();
        project.setIdentity(id);

        CommandResources commandResources = new CommandResources();

        // Add activate command link
        commandResources.add(linkTo(ProjectController.class)
                .slash("projects")
                .slash(id)
                .slash("commands")
                .slash("activate")
                .withRel("activate"));

        // Add suspend command link
        commandResources.add(linkTo(ProjectController.class)
                .slash("projects")
                .slash(id)
                .slash("commands")
                .slash("suspend")
                .withRel("suspend"));

        return new Resource<>(commandResources);
    }

    public static class CommandResources extends ResourceSupport {
    }
}
