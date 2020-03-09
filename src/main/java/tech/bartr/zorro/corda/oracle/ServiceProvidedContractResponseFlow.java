package tech.bartr.zorro.corda.oracle;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;
import tech.bartr.zorro.corda.consumer.flows.SLAContractRequestFlow;
import tech.bartr.zorro.corda.contract.ServiceProvidedContractState;
import tech.bartr.zorro.corda.provider.flows.ServiceProvidedContractRequestFlow;

@InitiatedBy(ServiceProvidedContractRequestFlow.class)
public class ServiceProvidedContractResponseFlow extends SignTransactionFlow {

    public ServiceProvidedContractResponseFlow(FlowSession flowSession) {
        super(flowSession);
    }

    @Override
    @Suspendable
    protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {
        ServiceProvidedContractState state = (ServiceProvidedContractState) stx.getCoreTransaction().getOutputs().get(0).getData();
        try {
            if (!getServiceHub().cordaService(LivenessCheckOracleService.class).livenessCheck(state.getUrl())) {
                throw new FlowException("Could not sign contract: Service not Live");
            }
        } catch (Exception e){
            throw new FlowException("Could not sign contract: Error checking liveness");
        }
    }
}
