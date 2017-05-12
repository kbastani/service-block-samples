package demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Enable JPA auditing on an empty configuration class to disable auditing on
 *
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories("demo.project")
public class JpaConfig {
}
