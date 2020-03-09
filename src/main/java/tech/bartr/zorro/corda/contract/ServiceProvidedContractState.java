package tech.bartr.zorro.corda.contract;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(ServiceProvidedContract.class)
public class ServiceProvidedContractState implements ContractState {

    private String url;

    private String serviceType;

    private SLAContractState slaContractState;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getUrl() {
        return url;
    }

    public SLAContractState getSlaContractState() {
        return slaContractState;
    }

    public void setSlaContractState(SLAContractState slaContractState) {
        this.slaContractState = slaContractState;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(slaContractState.getConsumer(), slaContractState.getProvider(), slaContractState.getOracle());
    }
}
