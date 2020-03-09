package tech.bartr.zorro.corda.provider.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.StateAndRef;
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
import tech.bartr.zorro.corda.contract.provision.ServiceProvidedContract;
import tech.bartr.zorro.corda.contract.sla.SLAContract;
import tech.bartr.zorro.corda.contract.sla.SLAContractState;
import tech.bartr.zorro.corda.contract.provision.ServiceProvidedContractState;

import java.util.Collections;

@InitiatingFlow
@StartableByRPC
public class ServiceProvidedContractRequestFlow extends FlowLogic<Void> {

    private SLAContractState parentContract;

    private StateAndRef<SLAContractState> inputState;

    public SLAContractState getParentContract() {
        return parentContract;
    }

    public void setParentContract(StateAndRef<SLAContractState> stateAndRef) {
        this.parentContract = stateAndRef.getState().getData();
        this.inputState = stateAndRef;
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
                .addInputState(inputState)
                .addOutputState(contractState, ServiceProvidedContract.ID)
                .addCommand(new ServiceProvidedContractState.Commands.INITIATE(), getOurIdentity().getOwningKey(), parentContract.getOracle().getOwningKey());

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
