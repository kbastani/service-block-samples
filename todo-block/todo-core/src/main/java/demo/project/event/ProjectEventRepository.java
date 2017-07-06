package demo.project.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectEventRepository extends JpaRepository<ProjectEvent, Long> {
    List<ProjectEvent> findEventsByProjectId(@Param("projectId") Long projectId);
}
