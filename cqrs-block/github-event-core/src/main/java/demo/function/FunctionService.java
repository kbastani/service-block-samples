package demo.function;

import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.model.LogType;
import demo.project.Project;

import java.util.Map;

public interface FunctionService {

    /* Command handler functions */
    @LambdaFunction(functionName="project-created", logType = LogType.Tail)
    Project projectCreated(Map event);

    @LambdaFunction(functionName="project-committed", logType = LogType.Tail)
    Project projectCommitted(Map event);

    /* Query handler functions */
    @LambdaFunction(functionName="tight-coupling-query", logType = LogType.Tail)
    Map<String, Object> tightCouplingQuery(Map event);
}
