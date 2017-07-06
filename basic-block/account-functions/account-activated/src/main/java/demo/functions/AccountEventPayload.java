package demo.functions;

import java.util.List;

public class AccountEventPayload {
    private List<AccountEvent> events;
    private Account account;

    public AccountEventPayload() {
    }

    public List<AccountEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AccountEvent> events) {
        this.events = events;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
