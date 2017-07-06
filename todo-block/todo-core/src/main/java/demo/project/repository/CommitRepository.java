package demo.project.repository;

import demo.project.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findCommitsByProjectId(@Param("projectId") Long projectId);
}
