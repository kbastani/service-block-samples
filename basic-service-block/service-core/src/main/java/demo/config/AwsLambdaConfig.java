package demo.config;

import amazon.aws.AWSLambdaConfigurerAdapter;
import com.amazonaws.services.lambda.AWSLambda;
import demo.function.LambdaFunctionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("development")
public class AwsLambdaConfig {

    @Bean
    public FunctionInvoker lambdaInvoker(AWSLambdaConfigurerAdapter configurerAdapter) {
        LambdaFunctionService lambdaFunctionService = configurerAdapter.getFunctionInstance(LambdaFunctionService.class);
        return new FunctionInvoker(lambdaFunctionService, configurerAdapter.getLambdaClient());
    }

    public static class FunctionInvoker {
        FunctionInvoker(LambdaFunctionService lambdaFunctionService, AWSLambda awsLambda) {
            this.lambdaFunctionService = lambdaFunctionService;
            this.awsLambda = awsLambda;
        }

        private final LambdaFunctionService lambdaFunctionService;
        private final AWSLambda awsLambda;

        public LambdaFunctionService getLambdaFunctionService() {
            return lambdaFunctionService;
        }

        public AWSLambda getAwsLambda() {
            return awsLambda;
        }
    }
}
