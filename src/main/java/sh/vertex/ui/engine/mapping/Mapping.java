package sh.vertex.ui.engine.mapping;

import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping data model
 *
 * Includes data about plain text names in relation to proguard obfuscated foreign names
 */
public class Mapping {

    /* Base proxy information */
    private final Class<? extends Proxy> proxy;
    private Class<?> proxyImplementation;
    private Class<?> internalClass;

    /* Mapped fields and methods */
    private final Map<String, Method> customMappings;

    public Mapping(Class<? extends Proxy> proxy) {
        this.proxy = proxy;
        this.customMappings = new HashMap<>();
    }

    public Class<? extends Proxy> getProxy() {
        return proxy;
    }

    public Class<?> getInternalClass() {
        return internalClass;
    }

    public Class<?> getProxyImplementation() {
        return proxyImplementation;
    }

    public void setProxyImplementation(Class<?> proxyImplementation) {
        this.proxyImplementation = proxyImplementation;
    }

    public void setInternalClass(Class<?> internalClass) {
        this.internalClass = internalClass;
    }

    public String getInternalName() {
        return this.getInternalClass().getName().replace('.', '/');
    }

    public Map<String, Method> getCustomMappings() {
        return customMappings;
    }
}
