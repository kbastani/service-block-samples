package demo.functions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.function.Function;

@SpringBootApplication
public class AccountActivatedFunction {

    public static void main(String[] args) {
        SpringApplication.run(AccountActivatedFunction.class, args);
    }

    @Bean
    public Function<AccountEvent, Account> function() {
        return accountEvent -> {
            // Get event log from payload
            List<AccountEvent> events =
                    (List<AccountEvent>)accountEvent.getPayload()
                            .getOrDefault("events", null);

            // Get account from the payload
            Account account = (Account)accountEvent.getPayload()
                    .getOrDefault("account", null);

            if(events != null && account != null) {
                // Get the most recent event
                AccountEvent lastEvent = events.stream().findFirst().get();

                if(lastEvent.getType() != AccountEventType.ACCOUNT_ACTIVATED) {
                    account.setStatus(AccountStatus.ACCOUNT_ACTIVATED);
                } else {
                    throw new RuntimeException("Account already suspended");
                }
            } else {
                throw new RuntimeException("Payload did not supply valid account payload");
            }

            return account;
        };
    }
}
