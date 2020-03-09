package tech.bartr.zorro.corda.contract;

import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class SLAContract implements Contract {

    public static final String ID = "tech.bartr.zorro.corda.contract.SLAContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
         // TODO Add validation steps...
         // 1. Check inputs/output states
         // 2. Ensure contract state has all the required fields.
    }
}
