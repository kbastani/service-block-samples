package demo.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL)
    private List<Commit> commits = new ArrayList<>();

    public Project() {
        status = ProjectStatus.PROJECT_CREATED;
    }

    public Project(String name) {
        this();
        this.name = name;
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

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                '}';
    }
}
