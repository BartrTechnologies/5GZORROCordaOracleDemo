package tech.bartr.zorro.corda.provider.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;
import tech.bartr.zorro.corda.consumer.flows.SLAContractRequestFlow;

@InitiatedBy(SLAContractRequestFlow.class)
public class SLAContractResponseFlow extends SignTransactionFlow {

    public SLAContractResponseFlow(FlowSession flowSession) {
        super(flowSession);
    }

    @Override
    @Suspendable
    protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
        // For Demo purposes we accept all transactions and sign.
        System.out.println("We accepted the tx");
    }
}
