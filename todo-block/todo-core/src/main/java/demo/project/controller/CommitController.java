package demo.project.controller;

import demo.event.EventService;
import demo.project.Commit;
import demo.project.repository.CommitRepository;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/v1")
public class CommitController {

    private final CommitRepository commitRepository;
    private final EventService eventService;

    public CommitController(CommitRepository commitRepository, EventService eventService) {
        this.commitRepository = commitRepository;
        this.eventService = eventService;
    }

    @PutMapping(path = "/commits/{id}")
    public ResponseEntity updateCommit(@RequestBody Commit commit, @PathVariable Long id) {
        return Optional.ofNullable(updateCommitResource(id, commit))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Commit update failed"));
    }

    @RequestMapping(path = "/commits/{id}")
    public ResponseEntity getCommit(@PathVariable Long id) {
        return Optional.ofNullable(commitRepository.findById(id).get())
                .map(this::getCommitResource)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private Resource<Commit> updateCommitResource(Long id, Commit commit) {
        commit.setIdentity(id);
        return getCommitResource(commitRepository.save(commit));
    }

    private Resource<Commit> getCommitResource(Commit commit) {
        Assert.notNull(commit, "Commit must not be null");

        if (!commit.hasLink("project")) {
            // Add command link
            commit.add(linkTo(ProjectController.class)
                    .slash("projects")
                    .slash(commit.getProjectId())
                    .withRel("project"));
        }

        return new Resource<>(commit);
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
}
