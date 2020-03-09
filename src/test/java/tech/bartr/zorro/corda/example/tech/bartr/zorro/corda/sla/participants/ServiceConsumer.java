package tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla.participants;

import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.DataFeed;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;
import tech.bartr.zorro.corda.consumer.flows.SLAContractRequestFlow;
import tech.bartr.zorro.corda.contract.SLAContractState;
import tech.bartr.zorro.corda.contract.ServiceProvidedContractState;
import tech.bartr.zorro.corda.market.MarketPlace;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServiceConsumer {
    private CordaX500Name name;

    private StartedMockNode node;

    private String requestServiceURL = "http://localhost:8080/AUTHENTICATION";

    private final CountDownLatch latch = new CountDownLatch(1);

    public void init(MockNetwork mockNetwork) {
        name = new CordaX500Name("London Borough Council", "London", "GB");
        node = mockNetwork.createPartyNode(name);
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        DataFeed feed = node.getServices().getVaultService().trackBy(ContractState.class);
        feed.getUpdates().subscribe((contractEvent) -> {
            if (contractEvent instanceof Vault.Update) {
                Vault.Update update = (Vault.Update) contractEvent;
                StateAndRef stateAndRef = (StateAndRef) update.getProduced().iterator().next();
                if (stateAndRef.getState().getData() instanceof SLAContractState) {
                    System.out.println("Contract Signed.");
                    // TODO Do some business logic...
                } else if (stateAndRef.getState().getData() instanceof ServiceProvidedContractState) {
                    latch.countDown();
                    // TODO Do some business logic...
                }
            }
        });
    }

    public void requestService(Party oracle) throws InterruptedException, ExecutionException, TimeoutException {
        SLAContractRequestFlow requestFlow = new SLAContractRequestFlow();
        requestFlow.setProvider(MarketPlace.locateServiceProvider(MarketPlace.ServiceType.AUTHENTICATION));
        requestFlow.setOracle(oracle);
        requestFlow.setServiceId(MarketPlace.ServiceType.AUTHENTICATION.toString());
        requestFlow.setServiceUrl(requestServiceURL);
        CordaFuture future = node.startFlow(requestFlow);
        future.get(60, TimeUnit.SECONDS);

        latch.await(120, TimeUnit.SECONDS);
    }

    public String getRequestedServiceURL() {
        return requestServiceURL;
    }
}
