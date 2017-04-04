package demo.event;

import demo.account.Account;
import demo.account.AccountRepository;
import demo.function.AccountFunctionService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventService {

    final private Logger log = Logger.getLogger(EventService.class);
    final private AccountRepository accountRepository;
    final private AccountEventRepository accountEventRepository;
    final private AccountFunctionService accountFunctionService;

    public EventService(AccountRepository accountRepository,
                        AccountEventRepository accountEventRepository,
                        AccountFunctionService accountFunctionService) {
        this.accountRepository = accountRepository;
        this.accountEventRepository = accountEventRepository;
        this.accountFunctionService = accountFunctionService;
    }

    public Account apply(AccountEvent accountEvent) {
        Assert.notNull(accountEvent.getAccountId(),
                "Account event must contain a valid account id");

        // Get the account referenced by the event
        Account account = accountRepository.findOne(accountEvent.getAccountId());
        Account updatedAccount = account;
        Assert.notNull(account, "An account for that ID does not exist");

        // Get a history of events for this account
        List<AccountEvent> events = accountEventRepository
                .findEventsByAccountId(accountEvent.getAccountId());

        // Sort the events reverse chronological
        events.sort(Comparator.comparing(AccountEvent::getCreatedAt).reversed());

        // Route requests to serverless functions
        switch (accountEvent.getType()) {
            case ACCOUNT_ACTIVATED:
                updatedAccount = accountFunctionService
                        .accountActivated(getAccountEventMap(accountEvent, events, account));
                break;
            case ACCOUNT_SUSPENDED:
                updatedAccount = accountFunctionService
                        .accountSuspended(getAccountEventMap(accountEvent, events, account));
                break;
        }

        log.info(account.toString());

        // Add the event and save the new account status
        addEvent(accountEvent, account);

        account.setStatus(updatedAccount.getStatus());
        account = accountRepository.save(account);

        return account;
    }

    private AccountEvent addEvent(AccountEvent accountEvent, Account account) {
        accountEvent = accountEventRepository.save(accountEvent);
        account.getEvents().add(accountEvent);
        return accountEvent;
    }

    private Map<String, Object> getAccountEventMap(AccountEvent event, List<AccountEvent> events, Account account) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("accountEvent", event);
        eventMap.put("eventLog", events);
        eventMap.put("account", account);
        return eventMap;
    }
}
