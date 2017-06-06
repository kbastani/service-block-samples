package github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class auto-configures a {@link GitHubTemplate} bean.
 *
 * @author kbastani
 */
@Configuration
@ConditionalOnMissingBean(GitHubTemplate.class)
@EnableConfigurationProperties(GitHubProperties.class)
public class GitHubAutoConfiguration {

    @Autowired
    private GitHubProperties gitHubProperties;

    @Bean
    protected GitHubTemplate gitHubTemplate() {
        return new GitHubTemplate(gitHubProperties);
    }
}
