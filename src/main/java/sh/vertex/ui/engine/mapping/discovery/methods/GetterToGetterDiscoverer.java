package sh.vertex.ui.engine.mapping.discovery.methods;

import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class GetterToGetterDiscoverer extends MethodDiscoverer {

    @Override
    public Method tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) {
        Class<?> proxyReturns = proxyMethod.getReturnType();
        boolean resIsProxy = Proxy.class.isAssignableFrom(proxyReturns);
        Class<?> returnType = resIsProxy ? findMappingsByProxy(proxyReturns.asSubclass(Proxy.class)).getInternalClass() : proxyReturns;

        return Stream.of(mapping.getInternalClass().getDeclaredMethods())
                .filter(m -> m.getReturnType() == returnType && m.getParameterCount() == 0)
                .findAny().orElse(null);
    }
}
