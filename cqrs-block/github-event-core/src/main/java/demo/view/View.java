package demo.view;

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

    public View() {
    }

    public View(String id, String viewName) {
        this.id = id;
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
}
