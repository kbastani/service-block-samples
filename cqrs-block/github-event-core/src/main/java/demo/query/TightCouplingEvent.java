package demo.query;

import demo.view.View;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EntityListeners;


@Document
@EntityListeners(AuditingEntityListener.class)
public class TightCouplingEvent {

    @Id
    private String id;
    private Long projectId;
    private View view;

    @CreatedDate
    private Long createdDate;

    @LastModifiedDate
    private Long lastModifiedDate;

    public TightCouplingEvent() {
    }

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

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "TightCouplingEvent{" +
                "id='" + id + '\'' +
                ", projectId=" + projectId +
                ", view=" + view +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
