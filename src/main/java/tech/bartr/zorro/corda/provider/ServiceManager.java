package tech.bartr.zorro.corda.provider;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import tech.bartr.zorro.corda.market.MarketPlace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServiceManager {

    private static final List<MarketPlace.ServiceType> SERVICE_TYPES = Arrays.asList(MarketPlace.ServiceType.AUTHENTICATION);

    private static final Map<String, Server> SERVICES = new HashMap();

    private ServiceManager() {
    };

    public static String startService(String serviceType, String serviceUrl) throws Exception {
        if (getServiceTypes().contains(MarketPlace.ServiceType.valueOf(serviceType))) {
            URL url = new URL(serviceUrl);
            Server server = new Server(url.getPort());
            server.setHandler(new LivenessHandler(url.getPath()));
            server.start();

            String serviceId = UUID.randomUUID().toString();
            SERVICES.put(serviceId, server);
            return serviceId;
        } else {
            throw new IllegalArgumentException("Service Type not Found");
        }
    }

    public static void stopService(String serviceId) throws Exception {
        SERVICES.get(serviceId).stop();
    }

    public static List<MarketPlace.ServiceType> getServiceTypes() {
        return SERVICE_TYPES;
    }
    private static class LivenessHandler extends AbstractHandler {

        private final String servicePath;

        public LivenessHandler(String servicePath) {
            super();
            this.servicePath = servicePath;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException
        {
            if (target.equals(servicePath)) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("{ \"status\": \"ok\"}");
                baseRequest.setHandled(true);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}
