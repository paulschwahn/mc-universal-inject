package sh.vertex.ui.engine.mapping.discovery.methods;

import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class GetterToGetterDiscoverer extends MethodDiscoverer {

    @Override
    public Method tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) {
        return Stream.of(mapping.getInternalClass().getDeclaredMethods())
                .filter(m -> m.getReturnType() == resolve(proxyMethod.getReturnType()) && m.getParameterCount() == 0)
                .findAny().orElse(null);
    }
}
