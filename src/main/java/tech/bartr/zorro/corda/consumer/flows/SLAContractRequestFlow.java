package tech.bartr.zorro.corda.consumer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
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

import java.util.Collections;

@InitiatingFlow
@StartableByRPC
public class SLAContractRequestFlow extends FlowLogic<Void> {

    private Party provider;

    private Party oracle;

    private String serviceId;

    private String serviceUrl;

    public Party getProvider() {
        return provider;
    }

    public void setProvider(Party provider) {
        this.provider = provider;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Party getOracle() {
        return oracle;
    }

    public void setOracle(Party oracle) {
        this.oracle = oracle;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {

        // Find a notary to finalise the Tx
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Create Output State
        SLAContractState slaContractState = new SLAContractState();
        slaContractState.setConsumer(getOurIdentity());
        slaContractState.setProvider(provider);
        slaContractState.setServiceType(serviceId);
        slaContractState.setServiceUrl(serviceUrl);
        slaContractState.setOracle(oracle);

        // Create Tx Builder and Add the Components
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(slaContractState, SLAContract.ID)
                .addCommand(new SLAContractState.Commands.INITIATE(), provider.getOwningKey());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(txBuilder);

        FlowSession flowSession = initiateFlow(provider);
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(signedTransaction, ImmutableList.of(flowSession)));
        subFlow(new FinalityFlow(stx, Collections.singleton(provider)));
        return null;
    }
}
