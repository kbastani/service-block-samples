package demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@EnableMongoAuditing
@Profile({"development", "cloud"})
public class MongoConfig extends AbstractCloudConfig {

    @Bean
    public MongoDbFactory mongoFactory() {
        return connectionFactory().mongoDbFactory();
    }

    @Bean
    CommandLineRunner commandLineRunner(MongoOperations operations) {
        return (args) -> {
            // Setup the streaming data endpoint
            if (operations.collectionExists("tightCouplingEvent")) {
                operations.dropCollection("tightCouplingEvent");
            }
            CollectionOptions options = new CollectionOptions(5242880, 5000, true);
            operations.createCollection("tightCouplingEvent", options);
        };
    }

}
