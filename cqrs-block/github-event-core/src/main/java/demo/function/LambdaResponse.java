package demo.function;

/**
 * The lambda response object containing a payload and/or error.
 *
 * @param <T> is the payload time
 */
public class LambdaResponse<T> {

    private Exception exception;
    private T payload;

    public LambdaResponse(Exception exception, T payload) {
        this.exception = exception;
        this.payload = payload;
    }

    public LambdaResponse(Exception exception) {
        this.exception = exception;
    }

    public LambdaResponse(T payload) {
        this.payload = payload;
    }

    public Exception getException() {
        return exception;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "LambdaResponse{" +
                "exception=" + exception +
                ", payload=" + payload +
                '}';
    }
}
