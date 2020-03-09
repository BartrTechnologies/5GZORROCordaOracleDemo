package tech.bartr.zorro.corda.contract.provision;

import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import tech.bartr.zorro.corda.contract.sla.SLAContractState;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ServiceProvidedContract implements Contract {

    public static final String ID = "tech.bartr.zorro.corda.contract.provision.ServiceProvidedContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("Number of inputs should be 1",
                    tx.getInputs().size() == 1);

            require.using("Number of outputs should be 1",
                    tx.getOutputs().size() == 1);

            final SLAContractState slaContractState = (SLAContractState) tx.getInput(0);
            final ServiceProvidedContractState serviceProvidedContractState = (ServiceProvidedContractState) tx.getOutput(0);

            require.using("Endpoint does not match parent contract",
                    slaContractState.getServiceUrl().equals(serviceProvidedContractState.getUrl()));

            require.using("Service Type does not match parent contract",
                    slaContractState.getServiceType().equals(serviceProvidedContractState.getServiceType()));

            require.using("The Agreed Oracle must sign the Tx",
                    tx.getCommand(0).getSigners().contains(slaContractState.getOracle().getOwningKey()));

            require.using("The Service Provider must sign the Tx",
                    tx.getCommand(0).getSigners().contains(slaContractState.getProvider().getOwningKey()));

            return null;
        });
    }
}
