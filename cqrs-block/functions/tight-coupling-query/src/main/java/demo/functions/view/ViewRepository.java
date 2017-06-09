package demo.functions.view;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewRepository extends MongoRepository<View, String> {

    List<View> findViewsByViewName(@Param("viewName") String viewName);
}
