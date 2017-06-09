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
    private Long captures = 0L;
    private List<String> fileIds = new ArrayList<>();

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

    public Long getCaptures() {
        return captures;
    }

    public void setCaptures(Long captures) {
        this.captures = captures;
    }

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }

    @Override
    public String toString() {
        return "View{" +
                "id='" + id + '\'' +
                ", viewName='" + viewName + '\'' +
                ", projectId=" + projectId +
                ", matches=" + matches +
                ", captures=" + captures +
                ", fileIds=" + fileIds +
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
        if (captures != null ? !captures.equals(view.captures) : view.captures != null) return false;
        return fileIds != null ? fileIds.equals(view.fileIds) : view.fileIds == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (viewName != null ? viewName.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        result = 31 * result + (matches != null ? matches.hashCode() : 0);
        result = 31 * result + (captures != null ? captures.hashCode() : 0);
        result = 31 * result + (fileIds != null ? fileIds.hashCode() : 0);
        return result;
    }
}
