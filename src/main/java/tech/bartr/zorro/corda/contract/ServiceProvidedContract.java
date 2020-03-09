package tech.bartr.zorro.corda.contract;

import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class ServiceProvidedContract implements Contract {

    public static final String ID = "tech.bartr.zorro.corda.contract.ServiceProvided";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        checkSignature(tx);
    }

    private void checkSignature(LedgerTransaction tx) {
        // TODO Check that the Oracle on the parent contract has signed the Tx
    }
}
