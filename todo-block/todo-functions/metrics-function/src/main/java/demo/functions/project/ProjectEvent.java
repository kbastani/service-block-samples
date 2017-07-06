package demo.functions.project;

import java.util.Map;

/**
 * The domain event {@link ProjectEvent} tracks the type and state of events as applied to the {@link Project} domain
 * object. This event resource can be used to event source the aggregate state of {@link Project}.
 * <p>
 * This event resource also provides a transaction log that can be used to append actions to the event.
 *
 * @author Kenny Bastani
 */
public class ProjectEvent {

    private Long eventId;
    private ProjectEventType type;
    private Map<String, Commit> payload;
    private Long projectId;
    private Long createdAt;
    private Long lastModified;

    public ProjectEvent() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public ProjectEventType getType() {
        return type;
    }

    public void setType(ProjectEventType type) {
        this.type = type;
    }

    public Map<String, Commit> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Commit> payload) {
        this.payload = payload;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
}
