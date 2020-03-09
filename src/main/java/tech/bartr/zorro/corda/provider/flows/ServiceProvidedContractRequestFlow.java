package tech.bartr.zorro.corda.provider.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.CollectSignaturesFlow;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import tech.bartr.zorro.corda.contract.SLAContract;
import tech.bartr.zorro.corda.contract.SLAContractState;
import tech.bartr.zorro.corda.contract.ServiceProvidedContractState;

import java.util.Collections;

@InitiatingFlow
@StartableByRPC
public class ServiceProvidedContractRequestFlow extends FlowLogic<Void> {

    private SLAContractState parentContract;

    public SLAContractState getParentContract() {
        return parentContract;
    }

    public void setParentContract(SLAContractState parentContract) {
        this.parentContract = parentContract;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {

        // Find a notary to finalise the Tx
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Create Output State
        ServiceProvidedContractState contractState = new ServiceProvidedContractState();
        contractState.setServiceType(parentContract.getServiceType());
        contractState.setUrl(parentContract.getServiceUrl());
        contractState.setSlaContractState(parentContract);

        // Create Tx Builder and Add the Components
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(contractState, SLAContract.ID)
                .addCommand(new SLAContractState.Commands.INITIATE(), getOurIdentity().getOwningKey(), parentContract.getOracle().getOwningKey());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(txBuilder);

        FlowSession flowSession = initiateFlow(parentContract.getOracle());
        try {
            SignedTransaction stx = subFlow(new CollectSignaturesFlow(signedTransaction, ImmutableList.of(flowSession)));
            subFlow(new FinalityFlow(stx, Collections.singleton(parentContract.getOracle())));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
