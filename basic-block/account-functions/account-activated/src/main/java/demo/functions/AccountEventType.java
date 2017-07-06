package demo.functions;

/**
 * The {@link AccountEventType} represents a collection of possible events that describe
 * state transitions of {@link AccountStatus} on the {@link Account} aggregate.
 *
 * @author kbastani
 */
public enum AccountEventType {
    ACCOUNT_ACTIVATED,
    ACCOUNT_SUSPENDED
}
