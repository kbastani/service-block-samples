package demo.query;


import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface QueryEventRepository extends ReactiveCrudRepository<TightCouplingEvent, String> {

    Flux<TightCouplingEvent> findByProjectId(@Param("id") Long projectId);
}
