package sh.vertex.ui.engine.mapping;

import lombok.Data;
import lombok.Getter;
import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.Field;
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
    private final Map<Method, Method> mappedMethods;
    private final Map<Method, Field> mappedFields;

    public Mapping(Class<? extends Proxy> proxy) {
        this.proxy = proxy;
        this.customMappings = new HashMap<>();
        this.mappedMethods = new HashMap<>();
        this.mappedFields = new HashMap<>();
    }

    public String getInternalName() {
        return this.getInternalClass().getName().replace('.', '/');
    }

    public String getGeneratedProxy() {
        return "sh/vertex/ui/engine/proxy/proxies/" + this.proxy.getSimpleName() + "Proxy";
    }
}
