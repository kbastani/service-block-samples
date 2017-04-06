package demo.config;

import amazon.aws.AWSLambdaConfigurerAdapter;
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
        return new FunctionInvoker(lambdaFunctionService);
    }

    public static class FunctionInvoker {
        FunctionInvoker(LambdaFunctionService lambdaFunctionService) {
            this.lambdaFunctionService = lambdaFunctionService;
        }

        private final LambdaFunctionService lambdaFunctionService;

        public LambdaFunctionService getLambdaFunctionService() {
            return lambdaFunctionService;
        }
    }
}
