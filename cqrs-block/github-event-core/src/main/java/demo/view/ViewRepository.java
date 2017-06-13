package demo.view;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewRepository extends ReactiveMongoRepository<View, String> {

    List<View> findViewsByViewName(@Param("viewName") String viewName);
}
