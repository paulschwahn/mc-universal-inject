package sh.vertex.ui.engine.mapping.discovery;

import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public abstract class MethodDiscoverer {

    public abstract AccessibleObject tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) throws Throwable;

    public Mapping findMappingsByProxy(Class<? extends Proxy> proxy) {
        return UniversalClient.getInstance().getMappingService().findMappingsByProxy(proxy);
    }

    public Class<?> resolve(Class<?> check) {
        if (Proxy.class.isAssignableFrom(check)) {
            return findMappingsByProxy(check.asSubclass(Proxy.class)).getInternalClass();
        }
        return check;
    }
}
