package demo.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity extends ResourceSupport implements Serializable {

    @CreatedDate
    private Long createdAt;

    @LastModifiedDate
    private Long lastModified;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountEvent> events = new ArrayList<>();

    public AbstractEntity() {
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

    @JsonIgnore
    public List<AccountEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AccountEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                '}';
    }
}