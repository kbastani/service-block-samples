package github;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;

public class GitHubConfigTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void tearDown() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void defaultAdapter() throws IOException {
        load(EmptyConfiguration.class, "github.access-token=0159f43b8f");

        GitHubAutoConfiguration gitHubAutoConfiguration = this.context.getBean(GitHubAutoConfiguration.class);
        assertNotNull(gitHubAutoConfiguration.gitHubTemplate());
    }

    @Configuration
    static class EmptyConfiguration {
    }

    private void load(Class<?> config, String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(applicationContext, environment);
        applicationContext.register(config);
        applicationContext.register(GitHubAutoConfiguration.class);
        applicationContext.refresh();
        this.context = applicationContext;
    }
}
