package tech.bartr.zorro.corda.contract.sla;

import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class SLAContract implements Contract {

    public static final String ID = "tech.bartr.zorro.corda.contract.sla.SLAContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        requireThat(require -> {
            if (tx.getCommand(0).getValue() instanceof SLAContractState.Commands.INITIATE) {
                require.using("Number of inputs should be 0",
                        tx.getInputs().size() == 0);

                require.using("Number of outputs should be 1",
                        tx.getOutputs().size() == 1);

                final SLAContractState slaContractState = (SLAContractState) tx.getOutput(0);

                require.using("The Service Provider must sign the Tx",
                        tx.getCommand(0).getSigners().contains(slaContractState.getProvider().getOwningKey()));

                require.using("The Service Consumer must sign the Tx",
                        tx.getCommand(0).getSigners().contains(slaContractState.getConsumer().getOwningKey()));

            }
            return null;

        });
    }
}
