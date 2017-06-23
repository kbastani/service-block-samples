package demo.view;


import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ViewRepository extends ReactiveCrudRepository<View, String> {

    Flux<View> findViewsByViewName(@Param("viewName") String viewName);
}
