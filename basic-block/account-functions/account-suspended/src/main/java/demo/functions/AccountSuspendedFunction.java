package demo.functions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.function.Function;

@SpringBootApplication
public class AccountSuspendedFunction {

    public static void main(String[] args) {
        SpringApplication.run(AccountSuspendedFunction.class, args);
    }

    @Bean
    public Function<AccountEvent, Account> function() {
        return accountEvent -> {
            // Get event log from payload
            List<AccountEvent> events =
                    (List<AccountEvent>)accountEvent.getPayload()
                            .getOrDefault("events", null);

            // Get account
            Account account = (Account)accountEvent.getPayload()
                    .getOrDefault("account", null);

            if(events != null && account != null) {
                // Get the most recent event
                AccountEvent lastEvent = events.stream().findFirst().get();

                if(lastEvent.getType() != AccountEventType.ACCOUNT_SUSPENDED) {
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
