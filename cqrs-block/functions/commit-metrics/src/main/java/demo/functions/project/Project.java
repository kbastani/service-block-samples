package demo.functions.project;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {

    private Long id;
    private ProjectStatus status;

    private String owner;
    private String name;

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

}