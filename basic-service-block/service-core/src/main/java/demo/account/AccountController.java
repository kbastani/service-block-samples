package demo.account;

import demo.event.AccountEvent;
import demo.event.AccountEventRepository;
import demo.event.AccountEventType;
import demo.event.EventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/v1")
public class AccountController {

    private final AccountRepository accountRepository;
    private final EventService eventService;
    private final AccountEventRepository eventRepository;

    public AccountController(AccountRepository accountRepository, EventService eventService, AccountEventRepository eventRepository) {
        this.accountRepository = accountRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(path = "/accounts")
    public ResponseEntity getAccounts(@RequestBody(required = false) PageRequest pageRequest) {
        return new ResponseEntity<>(accountRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/accounts")
    public ResponseEntity createAccount(@RequestBody Account account) {
        return Optional.ofNullable(createAccountResource(account))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Account creation failed"));
    }

    @PutMapping(path = "/accounts/{id}")
    public ResponseEntity updateAccount(@RequestBody Account account, @PathVariable Long id) {
        return Optional.ofNullable(updateAccountResource(id, account))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Account update failed"));
    }

    @RequestMapping(path = "/accounts/{id}")
    public ResponseEntity getAccount(@PathVariable Long id) {
        return Optional.ofNullable(accountRepository.findOne(id))
                .map(this::getAccountResource)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/accounts/{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        try {
            accountRepository.delete(id);
        } catch (Exception ex) {
            throw new RuntimeException("Account deletion failed");
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/accounts/{id}/events")
    public ResponseEntity getAccountEvents(@PathVariable Long id) {
        return Optional.of(getAccountEventResources(id))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Could not get account events"));
    }

    @RequestMapping(path = "/accounts/{id}/events/{eventId}")
    public ResponseEntity getAccountEvent(@PathVariable Long id, @PathVariable Long eventId) {
        return Optional.of(getEventResource(eventId))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Could not get order events"));
    }

    @PostMapping(path = "/accounts/{id}/events")
    public ResponseEntity appendAccountEvent(@PathVariable Long id, @RequestBody AccountEvent event) {
        return Optional.ofNullable(appendEventResource(id, event))
                .map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))
                .orElseThrow(() -> new RuntimeException("Append account event failed"));
    }

    @RequestMapping(path = "/accounts/{id}/commands")
    public ResponseEntity getCommands(@PathVariable Long id) {
        return Optional.ofNullable(getCommandsResource(id))
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("The account could not be found"));
    }

    @RequestMapping(path = "/accounts/{id}/commands/activate")
    public ResponseEntity activate(@PathVariable Long id) {
        return Optional.ofNullable(accountRepository.findOne(id))
                .map(a -> eventService
                        .apply(new AccountEvent(AccountEventType.ACCOUNT_ACTIVATED, id)))
                .map(this::getAccountResource)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("The command could not be applied"));
    }

    @RequestMapping(path = "/accounts/{id}/commands/suspend")
    public ResponseEntity suspend(@PathVariable Long id) {
        return Optional.ofNullable(accountRepository.findOne(id))
                .map(a -> eventService
                        .apply(new AccountEvent(AccountEventType.ACCOUNT_SUSPENDED, id)))
                .map(this::getAccountResource)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("The command could not be applied"));
    }

    /**
     * Creates a new {@link Account} entity and persists the result to the repository.
     *
     * @param account is the {@link Account} model used to create a new account
     * @return a hypermedia resource for the newly created {@link Account}
     */
    private Resource<Account> createAccountResource(Account account) {
        Assert.notNull(account, "Account body must not be null");
        Assert.notNull(account.getEmail(), "Email is required");
        Assert.notNull(account.getFirstName(), "First name is required");
        Assert.notNull(account.getLastName(), "Last name is required");

        // Create the new account
        account = accountRepository.save(account);

        return getAccountResource(account);
    }

    /**
     * Update a {@link Account} entity for the provided identifier.
     *
     * @param id      is the unique identifier for the {@link Account} update
     * @param account is the entity representation containing any updated {@link Account} fields
     * @return a hypermedia resource for the updated {@link Account}
     */
    private Resource<Account> updateAccountResource(Long id, Account account) {
        account.setIdentity(id);
        return getAccountResource(accountRepository.save(account));
    }

    /**
     * Appends an {@link AccountEvent} domain event to the event log of the {@link Account}
     * aggregate with the specified accountId.
     *
     * @param accountId is the unique identifier for the {@link Account}
     * @param event     is the {@link AccountEvent} that attempts to alter the state of the {@link Account}
     * @return a hypermedia resource for the newly appended {@link AccountEvent}
     */
    private Resource<AccountEvent> appendEventResource(Long accountId, AccountEvent event) {
        Assert.notNull(event, "Event body must be provided");

        Account account = accountRepository.findOne(accountId);
        Assert.notNull(account, "Account could not be found");

        event.setAccountId(account.getIdentity());
        eventService.apply(event);

        return new Resource<>(event,
                linkTo(AccountController.class)
                        .slash("accounts")
                        .slash(accountId)
                        .slash("events")
                        .slash(event.getEventId())
                        .withSelfRel(),
                linkTo(AccountController.class)
                        .slash("accounts")
                        .slash(accountId)
                        .withRel("account")
        );
    }

    private AccountEvent getEventResource(Long eventId) {
        return eventRepository.findOne(eventId);
    }

    private List<AccountEvent> getAccountEventResources(Long id) {
        return eventRepository.findEventsByAccountId(id);
    }

    private LinkBuilder linkBuilder(String name, Long id) {
        Method method;

        try {
            method = AccountController.class.getMethod(name, Long.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return linkTo(AccountController.class, method, id);
    }

    /**
     * Get a hypermedia enriched {@link Account} entity.
     *
     * @param account is the {@link Account} to enrich with hypermedia links
     * @return is a hypermedia enriched resource for the supplied {@link Account} entity
     */
    private Resource<Account> getAccountResource(Account account) {
        Assert.notNull(account, "Account must not be null");

        if (!account.hasLink("commands")) {
            // Add command link
            account.add(linkBuilder("getCommands", account.getIdentity()).withRel("commands"));
        }

        if (!account.hasLink("events")) {
            // Add get events link
            account.add(linkBuilder("getAccountEvents", account.getIdentity()).withRel("events"));
        }

        return new Resource<>(account);
    }

    private ResourceSupport getCommandsResource(Long id) {
        Account account = new Account();
        account.setIdentity(id);

        CommandResources commandResources = new CommandResources();

        // Add activate command link
        commandResources.add(linkTo(AccountController.class)
                .slash("accounts")
                .slash(id)
                .slash("commands")
                .slash("activate")
                .withRel("activate"));

        // Add suspend command link
        commandResources.add(linkTo(AccountController.class)
                .slash("accounts")
                .slash(id)
                .slash("commands")
                .slash("suspend")
                .withRel("suspend"));

        return new Resource<>(commandResources);
    }

    public static class CommandResources extends ResourceSupport {
    }
}
