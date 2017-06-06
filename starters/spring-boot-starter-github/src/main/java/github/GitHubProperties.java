package github;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a configuration properties model for authenticating with GitHub.
 *
 * @author kbastani
 */
@Configuration
@ConfigurationProperties(prefix = "github")
public class GitHubProperties {

    private String accessToken;

    /**
     * A valid GitHub account's personal access token.
     *
     * @return a GitHub OAuth2 personal access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * A valid GitHub account's personal access token.
     *
     * @param accessToken is a valid GitHub account's personal access token.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    @Override
    public String toString() {
        return "GitHubProperties{" +
                "accessToken='" + accessToken + '\'' +
                '}';
    }
}
