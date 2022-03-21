package sh.vertex.ui.engine.mapping;

import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.mapping.discovery.methods.DescriptorDiscoverer;

import java.lang.reflect.Method;

public enum PopulationMethod {

    /**
     * Scenario:
     * Internal class has a method with signature boolean calculate(String test)
     * We want to call this method and find it by using its unique descriptor of (Ljava/lang/String;)Z,
     *
     * can also be used for getter calls
     */
    METHOD_BY_DESCRIPTOR("Method's Descriptor Discoverer", new DescriptorDiscoverer());

    private final String name;
    private final MethodDiscoverer discoverer;

    PopulationMethod(String name, MethodDiscoverer discoverer) {
        this.name = name;
        this.discoverer = discoverer;
    }

    public Method tryDiscover(Mapping mapping, Method proxyMethod, MethodGenerator generator) throws Throwable {
        return this.discoverer.tryDiscover(mapping, proxyMethod, generator);
    }

    public String getName() {
        return name;
    }
}
