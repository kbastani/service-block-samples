package github;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.EventService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class GitHubTemplate {

    private final GitHubProperties gitHubProperties;
    private GitHubClient client;

    public GitHubTemplate(GitHubProperties gitHubProperties) {
        Assert.notNull(gitHubProperties, "Properties bean must not be null");
        this.gitHubProperties = gitHubProperties;
        authenticate();
    }

    private void authenticate() {
        client = new GitHubClient();
        client.setOAuth2Token(gitHubProperties.getAccessToken());
    }

    public EventService eventService() {
        return new EventService(client);
    }

    public CommitService commitService() {
        return new CommitService(client);
    }
}
