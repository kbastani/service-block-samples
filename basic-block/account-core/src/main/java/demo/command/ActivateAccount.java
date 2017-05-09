package demo.command;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import demo.account.Account;
import demo.account.AccountStatus;
import demo.config.AwsLambdaConfig;
import demo.domain.LambdaResponse;
import demo.event.AccountEvent;
import demo.event.AccountEventType;
import demo.function.LambdaFunctionService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED;
import static demo.event.AccountEventType.ACCOUNT_ACTIVATED;

@Service
public class ActivateAccount {

    private final Logger log = Logger.getLogger(ActivateAccount.class);
    private final LambdaFunctionService functionService;

    public ActivateAccount(AwsLambdaConfig.FunctionInvoker functionService) {
        this.functionService = functionService.getLambdaFunctionService();
    }

    @HystrixCommand(fallbackMethod = "accountActivatedFallback", commandProperties = {
            @HystrixProperty(name = EXECUTION_TIMEOUT_ENABLED, value = "false")
    })
    public LambdaResponse<Account> apply(Map eventMap) {
        try {
            return new LambdaResponse<>(functionService.accountActivated(eventMap));
        } catch (Exception ex) {
            if (Objects.equals(ex.getMessage(), "Account already activated")) {
                return new LambdaResponse<>(ex, null);
            } else {
                log.error("Error invoking AWS Lambda function", ex);
                throw ex;
            }
        }
    }

    public LambdaResponse<Account> accountActivatedFallback(Map eventMap) {
        Account account = (Account) eventMap.get("account");
        List<AccountEvent> events = (List<AccountEvent>) eventMap.get("eventLog");
        AccountEvent accountEvent = (AccountEvent) eventMap.get("accountEvent");

        // Get the most recent event
        AccountEventType lastEvent = events.stream().findFirst()
                .map(AccountEvent::getType)
                .orElse(null);

        Assert.isTrue(lastEvent != ACCOUNT_ACTIVATED, "Account already activated");

        account.setStatus(AccountStatus.valueOf(accountEvent.getType().toString()));

        return new LambdaResponse<>(null, account);
    }
}
