package demo.project.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import demo.project.Project;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Map;

/**
 * The domain event {@link ProjectEvent} tracks the type and state of events as applied to the {@link Project} domain
 * object. This event resource can be used to event source the aggregate state of {@link Project}.
 * <p>
 * This event resource also provides a transaction log that can be used to append actions to the event.
 *
 * @author Kenny Bastani
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProjectEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private ProjectEventType type;

    @Transient
    @JsonIgnore
    private Project entity;

    @Transient
    private Map<String, Object> payload;

    private Long projectId;

    @CreatedDate
    private Long createdAt;

    @LastModifiedDate
    private Long lastModified;

    public ProjectEvent() {
    }

    public ProjectEvent(Long eventId) {
        this.eventId = eventId;
    }

    public ProjectEvent(ProjectEventType type) {
        this.type = type;
    }

    public ProjectEvent(ProjectEventType type, Long projectId) {
        this.type = type;
        this.projectId = projectId;
    }

    public ProjectEvent(ProjectEventType type, Project entity) {
        this.type = type;
        this.entity = entity;
        this.projectId = entity.getIdentity();
    }

    public ProjectEvent(ProjectEventType type, Project entity, Map<String, Object> payload) {
        this.type = type;
        this.entity = entity;
        this.payload = payload;
        this.projectId = entity.getIdentity();
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long id) {
        eventId = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public ProjectEventType getType() {
        return type;
    }

    public void setType(ProjectEventType type) {
        this.type = type;
    }

    public Project getEntity() {
        return entity;
    }

    public void setEntity(Project entity) {
        this.entity = entity;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
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
        return "ProjectEvent{" +
                "eventId=" + eventId +
                ", type=" + type +
                ", entity=" + entity +
                ", payload=" + (payload != null ? String.valueOf(payload.hashCode()) : null) +
                ", projectId=" + projectId +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                '}';
    }
}
