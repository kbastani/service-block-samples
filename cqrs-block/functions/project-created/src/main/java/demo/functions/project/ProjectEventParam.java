package demo.functions.project;

import java.util.List;

/**
 * Created by user on 6/9/17.
 */
public class ProjectEventParam {

    private Project project;
    private ProjectEvent projectEvent;
    private List<ProjectEvent> eventLog;

    public ProjectEventParam() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectEvent getProjectEvent() {
        return projectEvent;
    }

    public void setProjectEvent(ProjectEvent projectEvent) {
        this.projectEvent = projectEvent;
    }

    public List<ProjectEvent> getEventLog() {
        return eventLog;
    }

    public void setEventLog(List<ProjectEvent> eventLog) {
        this.eventLog = eventLog;
    }
}
