package demo.function;

import demo.command.ActivateAccount;
import demo.command.SuspendAccount;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandService {

    private final ActivateAccount activateAccount;
    private final SuspendAccount suspendAccount;

    public AccountCommandService(ActivateAccount activateAccount, SuspendAccount suspendAccount) {
        this.activateAccount = activateAccount;
        this.suspendAccount = suspendAccount;
    }

    public ActivateAccount getActivateAccount() {
        return activateAccount;
    }

    public SuspendAccount getSuspendAccount() {
        return suspendAccount;
    }
}
