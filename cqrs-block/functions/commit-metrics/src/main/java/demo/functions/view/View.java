package demo.functions.view;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "query")
public class View {

    @Id
    private String id;
    private String viewName;
    private Long projectId;
    private Integer matches = 0;
    private List<String> fileIds = new ArrayList<>();
    private Long createdAt;
    private Long lastModified;

    public View(String viewName) {
        this.viewName = viewName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getMatches() {
        return matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
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

    public void setLastModified(Long updatedAt) {
        this.lastModified = updatedAt;
    }

    @Override
    public String toString() {
        return "View{" +
                "id='" + id + '\'' +
                ", viewName='" + viewName + '\'' +
                ", projectId=" + projectId +
                ", matches=" + matches +
                ", fileIds=" + fileIds +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        View view = (View) o;

        if (id != null ? !id.equals(view.id) : view.id != null) return false;
        if (viewName != null ? !viewName.equals(view.viewName) : view.viewName != null) return false;
        if (projectId != null ? !projectId.equals(view.projectId) : view.projectId != null) return false;
        if (matches != null ? !matches.equals(view.matches) : view.matches != null) return false;
        if (fileIds != null ? !fileIds.equals(view.fileIds) : view.fileIds != null) return false;
        if (createdAt != null ? !createdAt.equals(view.createdAt) : view.createdAt != null) return false;
        return lastModified != null ? lastModified.equals(view.lastModified) : view.lastModified == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (viewName != null ? viewName.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        result = 31 * result + (matches != null ? matches.hashCode() : 0);
        result = 31 * result + (fileIds != null ? fileIds.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }
}
