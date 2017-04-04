package demo.function;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import demo.account.Account;
import demo.account.AccountStatus;
import demo.config.AwsLambdaConfig;
import demo.event.AccountEvent;
import demo.event.AccountEventType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager.EXECUTION_TIMEOUT_ENABLED;
import static demo.event.AccountEventType.ACCOUNT_ACTIVATED;
import static demo.event.AccountEventType.ACCOUNT_SUSPENDED;

@Service
public class AccountFunctionService {

    private final LambdaFunctionService functionService;
    final private Logger log = Logger.getLogger(AccountFunctionService.class);

    public AccountFunctionService(AwsLambdaConfig.FunctionInvoker functionService) {
        this.functionService = functionService.getLambdaFunctionService();
    }

    @HystrixCommand(fallbackMethod = "accountActivatedFallback", commandProperties = {
            @HystrixProperty(name = EXECUTION_TIMEOUT_ENABLED, value = "false")
    })
    public Account accountActivated(Map eventMap) {
        try {
            return functionService.accountActivated(eventMap);
        } catch (Exception ex) {
            log.error("Error invoking AWS Lambda function", ex);
            throw ex;
        }
    }

    public Account accountActivatedFallback(Map eventMap) {
        Account account = (Account) eventMap.get("account");
        List<AccountEvent> events = (List<AccountEvent>) eventMap.get("eventLog");
        AccountEvent accountEvent = (AccountEvent) eventMap.get("accountEvent");

        // Get the most recent event
        AccountEventType lastEvent = events.stream().findFirst()
                .map(AccountEvent::getType)
                .orElse(null);

        Assert.isTrue(lastEvent != ACCOUNT_ACTIVATED, "Account already activated");

        account.setStatus(AccountStatus.valueOf(accountEvent.getType().toString()));

        return account;
    }

    @HystrixCommand(fallbackMethod = "accountSuspendedFallback", commandProperties = {
            @HystrixProperty(name = EXECUTION_TIMEOUT_ENABLED, value = "false")
    })
    public Account accountSuspended(Map eventMap) {
        try {
            return functionService.accountSuspended(eventMap);
        } catch (Exception ex) {
            log.error("Error invoking AWS Lambda function", ex);
            throw ex;
        }
    }


    public Account accountSuspendedFallback(Map eventMap) {
        Account account = (Account) eventMap.get("account");
        List<AccountEvent> events = (List<AccountEvent>) eventMap.get("eventLog");
        AccountEvent accountEvent = (AccountEvent) eventMap.get("accountEvent");

        // Get the most recent event
        AccountEventType lastEvent = events.stream().findFirst()
                .map(AccountEvent::getType)
                .orElse(null);

        Assert.isTrue(lastEvent != ACCOUNT_SUSPENDED, "Account already suspended");

        account.setStatus(AccountStatus.valueOf(accountEvent.getType().toString()));

        return account;
    }
}
