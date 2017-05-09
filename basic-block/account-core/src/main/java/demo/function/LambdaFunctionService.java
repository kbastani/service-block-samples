package demo.function;

import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.model.LogType;
import demo.account.Account;

import java.util.Map;

public interface LambdaFunctionService {
    
    @LambdaFunction(functionName="account-activated", logType = LogType.Tail)
    Account accountActivated(Map event);

    @LambdaFunction(functionName="account-suspended", logType = LogType.Tail)
    Account accountSuspended(Map event);
}
