package tech.bartr.zorro.corda.contract.sla;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(SLAContract.class)
public class SLAContractState implements ContractState {

    private Party provider;

    private Party consumer;

    private Party oracle;

    private String serviceType;

    private String serviceUrl;

    public Party getProvider() {
        return provider;
    }

    public Party getConsumer() {
        return consumer;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setProvider(Party provider) {
        this.provider = provider;
    }

    public void setConsumer(Party consumer) {
        this.consumer = consumer;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(provider, consumer);
    }

    public interface Commands extends CommandData {
        class INITIATE implements SLAContractState.Commands {

        }
    }
}
