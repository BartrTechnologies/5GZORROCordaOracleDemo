package tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla.participants;

import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.DataFeed;
import net.corda.core.node.services.Vault;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;
import tech.bartr.zorro.corda.consumer.flows.SLAContractRequestFlow;
import tech.bartr.zorro.corda.contract.SLAContractState;
import tech.bartr.zorro.corda.provider.ServiceManager;
import tech.bartr.zorro.corda.provider.flows.SLAContractResponseFlow;
import tech.bartr.zorro.corda.market.MarketPlace;
import tech.bartr.zorro.corda.provider.flows.ServiceProvidedContractRequestFlow;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServiceProvider {

    private CordaX500Name name;

    private StartedMockNode node;

    private MockNetwork mockNetwork;

    public void init(MockNetwork mockNetwork) {
        this.mockNetwork = mockNetwork;

        name = new CordaX500Name("BT", "London", "GB");
        node = mockNetwork.createPartyNode(name);
        node.registerInitiatedFlow(SLAContractResponseFlow.class);

        MarketPlace.registerService(MarketPlace.ServiceType.AUTHENTICATION, node.getInfo().getLegalIdentities().get(0));
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        DataFeed feed = node.getServices().getVaultService().trackBy(ContractState.class);
        feed.getUpdates().subscribe((contractEvent) -> {
            if (contractEvent instanceof Vault.Update) {
                Vault.Update update = (Vault.Update) contractEvent;
                StateAndRef stateAndRef = (StateAndRef) update.getProduced().iterator().next();
                if (stateAndRef.getState().getData() instanceof SLAContractState) {
                    SLAContractState slaContractState = (SLAContractState) stateAndRef.getState().getData();
                    try {
                        ServiceManager.startService(slaContractState.getServiceType(), slaContractState.getServiceUrl());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    startServiceProvidedFlow(slaContractState);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void startServiceProvidedFlow(SLAContractState parentContract) throws InterruptedException, ExecutionException, TimeoutException {
        ServiceProvidedContractRequestFlow flow = new ServiceProvidedContractRequestFlow();
        flow.setParentContract(parentContract);
        mockNetwork.runNetwork();
        CordaFuture future = node.startFlow(flow);
        mockNetwork.runNetwork();
        future.get(60, TimeUnit.SECONDS);
    }
}
