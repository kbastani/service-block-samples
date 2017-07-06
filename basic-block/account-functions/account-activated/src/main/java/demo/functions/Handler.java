package demo.functions;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

/**
 * This is the request handler that maps to the function bean in {@link AccountActivatedFunction}
 *
 * @author Kenny Bastani
 */
public class Handler extends SpringBootRequestHandler<AccountEvent, Account> {
}
