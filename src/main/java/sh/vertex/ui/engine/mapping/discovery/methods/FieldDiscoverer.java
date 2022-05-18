package sh.vertex.ui.engine.mapping.discovery.methods;

import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * <p>Discoverer for classes containing only one field of a given type, for example class Minecraft
 * contains exactly one {@link sh.vertex.ui.engine.structure.entity.ClientPlayerEntity} type, making
 * it easily discoverable by this class.</p>
 *
 * <p>If multiple different fields of the same type are available, a random one gets chosen, resulting
 * in this discoverer being unstable.</p>
 *
 * @author Paul Schwahn
 * @since 21.03.2022
 */
public class FieldDiscoverer extends MethodDiscoverer {

    @Override
    public Field tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) {
        return Stream.of(mapping.getInternalClass().getDeclaredFields())
                .filter(f -> f.getType() == resolve(proxyMethod.getReturnType()))
                .findAny().orElse(null);
    }
}
