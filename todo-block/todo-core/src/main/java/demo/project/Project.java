package demo.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import demo.project.event.ProjectEvent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project extends AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ProjectStatus status;

    private String owner;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.MERGE)
    private List<Commit> commits = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<ProjectEvent> events = new ArrayList<>();

    public Project() {
        status = ProjectStatus.PROJECT_CREATED;
    }

    public Project(String name) {
        this();
        this.name = name;
    }

    public Project(Long id) {
        this.id = id;
    }

    @JsonProperty("projectId")
    public Long getIdentity() {
        return this.id;
    }

    public void setIdentity(Long id) {
        this.id = id;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public List<ProjectEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ProjectEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", status=" + status +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", commits=" + (commits != null ? String.valueOf(commits.hashCode()) : null) +
                '}';
    }
}
