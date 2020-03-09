package tech.bartr.zorro.corda.market;

import net.corda.core.identity.Party;

import java.util.HashMap;
import java.util.Map;

public class MarketPlace {

    public enum ServiceType {
        AUTHENTICATION
    }

    private static final Map<ServiceType, Party> SERVICES = new HashMap<>();

    public static void registerService(ServiceType serviceType, Party party) {
        SERVICES.put(serviceType, party);
    }

    public static Party locateServiceProvider(ServiceType serviceType) {
        return SERVICES.get(serviceType);
    }

}
