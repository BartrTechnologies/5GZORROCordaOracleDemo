package tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla;

import com.google.common.collect.ImmutableList;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.TestCordapp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla.participants.OracleService;
import tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla.participants.ServiceConsumer;
import tech.bartr.zorro.corda.example.tech.bartr.zorro.corda.sla.participants.ServiceProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DemoSLAServiceProvisionWithOracle {

    private ServiceProvider serviceProvider;

    private ServiceConsumer serviceConsumer;

    private OracleService oracleService;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final MockNetwork mockNetwork = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
            TestCordapp.findCordapp("tech.bartr.zorro.corda.contract"),
            TestCordapp.findCordapp("tech.bartr.zorro.corda.provider.flows"),
            TestCordapp.findCordapp("tech.bartr.zorro.corda.consumer.flows"),
            TestCordapp.findCordapp("tech.bartr.zorro.corda.oracle")
    )));

    @Before
    public void setup() {
        serviceConsumer = new ServiceConsumer();
        serviceConsumer.init(mockNetwork);
        serviceProvider = new ServiceProvider();
        serviceProvider.init(mockNetwork);
        oracleService = new OracleService();
        oracleService.init(mockNetwork);

        executorService.scheduleAtFixedRate(() -> mockNetwork.runNetwork(), 5, 5, TimeUnit.SECONDS);
    }

    @After
    public void cleanUp() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        mockNetwork.stopNodes();
    }

    @Test
    public void testNodeDeploys() throws Exception {
        serviceConsumer.requestService(oracleService.getIdentity());

        // Wait for the Service to be Provisioned
        // TODO Add ServiceProvisioned Event Handler
        Thread.sleep(10000);

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(serviceConsumer.getRequestedServiceURL()).build();
        Response response = httpClient.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());
    }
}
