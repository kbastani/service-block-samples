package demo;

import demo.config.AwsLambdaConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class AccountCoreTests {

	@MockBean
	private AwsLambdaConfig.FunctionInvoker functionInvoker;

	@Test
	public void contextLoads() {
	}

}
