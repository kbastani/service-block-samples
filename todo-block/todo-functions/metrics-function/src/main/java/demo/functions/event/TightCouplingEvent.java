package demo.functions.event;

import demo.functions.view.View;

public class TightCouplingEvent {

    private String id;
    private Long projectId;
    private View view;

    public TightCouplingEvent(Long projectId, View view) {
        this.projectId = projectId;
        this.view = view;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
