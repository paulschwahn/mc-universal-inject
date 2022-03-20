package sh.vertex.ui.engine.mapping.discovery.methods;

import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class DescriptorDiscoverer extends MethodDiscoverer {

    @Override
    public Method tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) {
        return Stream.of(mapping.getInternalClass().getDeclaredMethods())
                .filter(m -> {
                    if (m.getParameterCount() != proxyMethod.getParameterCount()) return false;
                    if (m.getReturnType() != resolve(proxyMethod.getReturnType())) return false;

                    for (int i = 0; i < m.getParameterCount(); i++)
                        if (m.getParameterTypes()[i] != resolve(proxyMethod.getParameterTypes()[i]))
                            return false;

                    return true;
                })
                .findAny().orElse(null);
    }
}
