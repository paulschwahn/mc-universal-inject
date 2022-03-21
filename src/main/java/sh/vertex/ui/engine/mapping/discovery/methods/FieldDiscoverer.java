package sh.vertex.ui.engine.mapping.discovery.methods;

import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class FieldDiscoverer extends MethodDiscoverer {

    @Override
    public Field tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) {
        return Stream.of(mapping.getInternalClass().getDeclaredFields())
                .filter(f -> f.getType() == resolve(proxyMethod.getReturnType()))
                .findAny().orElse(null);
    }
}
