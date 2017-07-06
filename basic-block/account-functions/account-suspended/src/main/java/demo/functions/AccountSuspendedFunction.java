package demo.functions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.function.Function;

import static demo.functions.AccountEventType.ACCOUNT_SUSPENDED;

@SpringBootApplication
public class AccountSuspendedFunction {

    public static void main(String[] args) {
        SpringApplication.run(AccountSuspendedFunction.class, args);
    }

    @Bean
    public Function<AccountEvent, Account> function() {
        return accountEvent -> {
            // Get event log from payload
            List<AccountEvent> events = accountEvent.getPayload().getEvents();

            // Get account
            Account account = accountEvent.getPayload().getAccount();

            if(events != null && account != null) {
                // Get the most recent event
                AccountEvent lastEvent = events.stream().findFirst().orElse(null);

                if(lastEvent == null || lastEvent.getType() != ACCOUNT_SUSPENDED) {
                    account.setStatus(AccountStatus.ACCOUNT_SUSPENDED);
                } else {
                    throw new RuntimeException("Account already suspended");
                }
            } else {
                throw new RuntimeException("Payload did not supply account events");
            }

            return account;
        };
    }
}
