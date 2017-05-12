package demo.query;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QueryRepository extends MongoRepository<QueryModel, String> {

    List<QueryModel> findQueryModelsByViewName(@Param("viewName") String viewName);
}
