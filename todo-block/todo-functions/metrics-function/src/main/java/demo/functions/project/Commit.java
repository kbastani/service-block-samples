package demo.functions.project;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Commit {

    private Long id;
    private CommitStatus status;
    private Long projectId;
    private List<File> files;
    private String author;
    private Long commitDate;
    private Long createdAt;
    private Long lastModified;

    public Commit() {
        status = CommitStatus.PUSHED;
    }

    public Commit(List<File> files) {
        this();
        this.files = files;
    }

    @JsonProperty("commitId")
    public Long getIdentity() {
        return this.id;
    }

    public void setIdentity(Long id) {
        this.id = id;
    }

    public CommitStatus getStatus() {
        return status;
    }

    public void setStatus(CommitStatus status) {
        this.status = status;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Long commitDate) {
        this.commitDate = commitDate;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id=" + id +
                ", status=" + status +
                ", projectId=" + projectId +
                ", files=" + files +
                '}';
    }
}
