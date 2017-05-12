package demo.query;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface QueryRepository extends MongoRepository<QueryModel, String> {
}
