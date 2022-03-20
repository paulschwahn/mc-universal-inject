package sh.vertex.ui.engine.mapping;

import sh.vertex.ui.engine.mapping.discovery.MethodDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.mapping.discovery.methods.DescriptorDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.methods.GetterToGetterDiscoverer;

import java.lang.reflect.Method;

public enum PopulationMethod {

    /**
     * Scenario:
     * Internal class has a getter called "getName()",
     * our proxy wants to call this getter and return the result.
     */
    GETTER_CALL("Getter to Getter Discoverer", new GetterToGetterDiscoverer()),

    /**
     * Scenario:
     * Internal class has a method with signature boolean calculate(String test)
     * We want to call this method and find it by using its unique descriptor of (Ljava/lang/String;)Z
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
