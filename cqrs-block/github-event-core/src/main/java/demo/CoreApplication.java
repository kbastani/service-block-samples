package demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableHypermediaSupport(type = {HypermediaType.HAL})
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
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


