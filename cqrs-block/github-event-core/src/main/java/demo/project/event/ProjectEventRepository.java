package demo.project.event;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectEventRepository extends PagingAndSortingRepository<ProjectEvent, Long> {
    List<ProjectEvent> findEventsByProjectId(@Param("projectId") Long projectId);
}
