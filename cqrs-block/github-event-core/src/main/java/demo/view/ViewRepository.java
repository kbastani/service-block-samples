package demo.view;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface ViewRepository extends ReactiveMongoRepository<View, String> {

    Flux<View> findViewsByViewName(@Param("viewName") String viewName);
}
