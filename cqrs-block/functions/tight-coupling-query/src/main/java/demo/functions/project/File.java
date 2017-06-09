package demo.functions.project;

import com.fasterxml.jackson.annotation.JsonProperty;

public class File {

    private Long id;
    private FileStatus status;
    private String fileName;
    private Long createdAt;
    private Long lastModified;

    public File() {
        status = FileStatus.CHANGED;
    }

    public File(String fileName) {
        this();
        this.fileName = fileName;
    }

    @JsonProperty("fileId")
    public Long getIdentity() {
        return this.id;
    }

    public void setIdentity(Long id) {
        this.id = id;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
        return "File{" +
                "id=" + id +
                ", status=" + status +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
