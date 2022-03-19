package sh.vertex.ui.engine.mapping.discovery;

import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.Method;

public abstract class MethodDiscoverer {

    public abstract Method tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) throws Throwable;

    public Mapping findMappingsByProxy(Class<? extends Proxy> proxy) {
        return UniversalClient.getInstance().getMappingService().findMappingsByProxy(proxy);
    }
}
