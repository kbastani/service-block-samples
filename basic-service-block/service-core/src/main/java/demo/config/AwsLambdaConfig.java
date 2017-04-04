package demo.config;

import amazon.aws.AWSLambdaConfigurerAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.function.LambdaFunctionService;
import demo.util.LambdaUtil;
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

    @Bean
    public LambdaUtil lambdaUtil(ObjectMapper objectMapper) {
        return new LambdaUtil(objectMapper);
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
