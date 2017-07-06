package demo.function;

import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.model.LogType;
import demo.account.Account;
import demo.account.AccountEvent;

public interface LambdaFunctionService {
    
    @LambdaFunction(functionName="account-activated", logType = LogType.Tail)
    Account accountActivated(AccountEvent event);

    @LambdaFunction(functionName="account-suspended", logType = LogType.Tail)
    Account accountSuspended(AccountEvent event);
}
