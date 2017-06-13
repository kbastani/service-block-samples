package demo.config;

import amazon.aws.AWSLambdaConfigurerAdapter;
import com.amazonaws.services.lambda.AWSLambda;
import demo.function.FunctionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"cloud", "development"})
public class AwsLambdaConfig {

    @Bean
    public FunctionInvoker lambdaInvoker(AWSLambdaConfigurerAdapter configurerAdapter) {
        FunctionService functionService = configurerAdapter.getFunctionInstance(FunctionService.class);
        return new FunctionInvoker(functionService, configurerAdapter.getLambdaClient());
    }

    public static class FunctionInvoker {
        FunctionInvoker(FunctionService functionService, AWSLambda awsLambda) {
            this.functionService = functionService;
            this.awsLambda = awsLambda;
        }

        private final FunctionService functionService;
        private final AWSLambda awsLambda;

        public FunctionService getFunctionService() {
            return functionService;
        }

        public AWSLambda getAwsLambda() {
            return awsLambda;
        }
    }
}
