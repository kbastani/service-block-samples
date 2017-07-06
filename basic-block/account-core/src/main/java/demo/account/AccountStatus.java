package demo.account;

/**
 * The {@link AccountStatus} describes the state of an {@link Account}.
 * The aggregate state of a {@link Account} is sourced from attached domain
 * events in the form of {@link AccountEvent}.
 *
 * @author kbastani
 */
public enum AccountStatus {
    ACCOUNT_ACTIVATED,
    ACCOUNT_SUSPENDED
}
