package demo.project.repository;

import demo.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findProjectByName(@Param("name") String name);
}
