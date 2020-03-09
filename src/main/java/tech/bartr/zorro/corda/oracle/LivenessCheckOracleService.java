package tech.bartr.zorro.corda.oracle;

import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@CordaService
public class LivenessCheckOracleService extends SingletonSerializeAsToken {

    private ServiceHub serviceHub;

    private OkHttpClient httpClient = new OkHttpClient();

    public LivenessCheckOracleService(ServiceHub serviceHub) {
        this.serviceHub = serviceHub;
    }

    public boolean livenessCheck(String endpointUrl) throws IOException {
        try {
            Request request = new Request.Builder().url(endpointUrl).build();
            Response response = httpClient.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }
}
