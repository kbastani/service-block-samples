package amazon.aws;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import org.springframework.stereotype.Component;

/**
 * Provides a configurer for invoking remote AWS Lambda functions using {@link LambdaInvokerFactory}.
 * This component also manages the authenticated session for an IAM user that has provided valid
 * access keys to access AWS resources.
 *
 * @author kbastani
 */
@Component
public class AWSLambdaConfigurerAdapter {

    private final AmazonProperties amazonProperties;

    /**
     * Create a new instance of the {@link AWSLambdaConfigurerAdapter} with the bucket name and access credentials
     */
    public AWSLambdaConfigurerAdapter(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    /**
     * Creates a proxy instance of a supplied interface that contains methods annotated with
     * {@link LambdaFunction}. Provides automatic credential support to authenticate with an IAM
     * access keys using {@link BasicSessionCredentials} auto-configured from Spring Boot
     * configuration properties in {@link AmazonProperties}.
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getFunctionInstance(Class<T> type) {
        return LambdaInvokerFactory.builder()
                .lambdaClient(AWSLambdaClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1)
                        .withCredentials(new LambdaCredentialsProvider(amazonProperties))
                        .build())
                .build(type);
    }

    public AWSLambda getLambdaClient() {
        return AWSLambdaClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new LambdaCredentialsProvider(amazonProperties))
                .build();
    }
}
