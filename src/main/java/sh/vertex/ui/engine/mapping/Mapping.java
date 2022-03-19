package sh.vertex.ui.engine.mapping;

import lombok.Data;
import lombok.Getter;
import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping data model
 *
 * Includes data about plain text names in relation to proguard obfuscated foreign names
 */
@Data
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

    public String getInternalName() {
        return this.getInternalClass().getName().replace('.', '/');
    }

}
