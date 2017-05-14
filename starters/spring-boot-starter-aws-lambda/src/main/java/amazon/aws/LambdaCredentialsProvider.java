package amazon.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;

import java.util.Date;

/**
 * Credential provider for AWS Lambda, with automatic refresh when credentials expire.
 *
 * @author Kenny Bastani
 */
public class LambdaCredentialsProvider implements AWSCredentialsProvider {

    private Credentials sessionCredentials;
    private final AmazonProperties amazonProperties;

    public LambdaCredentialsProvider(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    @Override
    public AWSCredentials getCredentials() {
        return getBasicSessionCredentials();
    }

    @Override
    public void refresh() {
        getBasicSessionCredentials();
    }

    /**
     * Get the basic session credentials for the template's configured IAM authentication keys
     *
     * @return a {@link BasicSessionCredentials} instance with a valid authenticated session token
     */
    private BasicSessionCredentials getBasicSessionCredentials() {

        // Create a new session token if the session is expired or not initialized
        if (sessionCredentials == null || sessionCredentials.getExpiration().before(new Date()))
            sessionCredentials = getSessionCredentials();

        // Create basic session credentials using the generated session token
        return new BasicSessionCredentials(sessionCredentials.getAccessKeyId(),
                sessionCredentials.getSecretAccessKey(),
                sessionCredentials.getSessionToken());
    }

    /**
     * Creates a new session credential that is valid for 12 hours
     *
     * @return an authenticated {@link Credentials} for the new session token
     */
    private Credentials getSessionCredentials() {
        // Create a new session with the user credentials for the service instance
        AWSSecurityTokenServiceClient stsClient =
                new AWSSecurityTokenServiceClient(new BasicAWSCredentials(
                        amazonProperties.getAws().getAccessKeyId(),
                        amazonProperties.getAws().getAccessKeySecret()));

        // Start a new session for managing a service instance's bucket
        GetSessionTokenRequest getSessionTokenRequest =
                new GetSessionTokenRequest().withDurationSeconds(43200);

        // Get the session token for the service instance's bucket
        sessionCredentials = stsClient.getSessionToken(getSessionTokenRequest).getCredentials();

        return sessionCredentials;
    }
}
