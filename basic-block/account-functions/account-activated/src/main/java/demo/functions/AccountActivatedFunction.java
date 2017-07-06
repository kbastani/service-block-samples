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
            List<AccountEvent> events = accountEvent.getPayload().getEvents();

            // Get account
            Account account = accountEvent.getPayload().getAccount();

            if(events != null && account != null) {
                // Get the most recent event
                AccountEvent lastEvent = events.stream().findFirst().get();

                if(lastEvent.getType() != AccountEventType.ACCOUNT_ACTIVATED) {
                    account.setStatus(AccountStatus.ACCOUNT_ACTIVATED);
                } else {
                    throw new RuntimeException("Account already activated");
                }
            } else {
                throw new RuntimeException("Payload did not supply account events");
            }

            return account;
        };
    }
}
