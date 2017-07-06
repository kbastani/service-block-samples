package demo.functions;

public class AccountEvent {

    private Long eventId;
    private AccountEventType type;
    private AccountEventPayload payload;
    private Long accountId;
    private Long createdAt;
    private Long lastModified;

    public AccountEvent() {
    }

    public AccountEvent(AccountEventType type) {
        this.type = type;
    }

    public AccountEvent(AccountEventType type, Long accountId) {
        this.type = type;
        this.accountId = accountId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long id) {
        eventId = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public AccountEventType getType() {
        return type;
    }

    public void setType(AccountEventType type) {
        this.type = type;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public AccountEventPayload getPayload() {
        return payload;
    }

    public void setPayload(AccountEventPayload payload) {
        this.payload = payload;
    }
}
