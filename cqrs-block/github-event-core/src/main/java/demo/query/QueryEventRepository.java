package demo.query;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface QueryEventRepository extends ReactiveMongoRepository<TightCouplingEvent, String> {
    Flux<TightCouplingEvent> findAllByProjectId(@Param("projectId") Long projectId);
}
