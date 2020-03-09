package tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla.participants;

import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;

public class OracleService {

    private CordaX500Name name;

    private StartedMockNode node;

    public void init(MockNetwork mockNetwork) {
        name = new CordaX500Name("Oracle", "London", "GB");
        node = mockNetwork.createPartyNode(name);
    }

    public Party getIdentity() {
        return node.getServices().getMyInfo().getLegalIdentities().get(0);
    }
}
