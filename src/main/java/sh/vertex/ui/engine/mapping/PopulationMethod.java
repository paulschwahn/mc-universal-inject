package sh.vertex.ui.engine.mapping;

import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.mapping.discovery.methods.FieldDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.methods.DescriptorDiscoverer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public enum PopulationMethod {

    /**
     * Scenario:
     * Internal class has a method with signature boolean calculate(String test)
     * We want to call this method and find it by using its unique descriptor of (Ljava/lang/String;)Z,
     *
     * Can also be used for getter or setter calls
     */
    METHOD_BY_DESCRIPTOR("Method's Descriptor Discoverer", new DescriptorDiscoverer()),

    /**
     * Scenario:
     * Internal class has a method with a field of type ClientPlayerEntity
     * We want to get or set this field to our likings using proxy getter or setter methods
     */
    FIELD("Field Discoverer", new FieldDiscoverer());

    private final String name;
    private final MethodDiscoverer discoverer;

    PopulationMethod(String name, MethodDiscoverer discoverer) {
        this.name = name;
        this.discoverer = discoverer;
    }

    public AccessibleObject tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) throws Throwable {
        return this.discoverer.tryDiscover(mapping, proxyMethod, generator);
    }

    public String getName() {
        return name;
    }
}
