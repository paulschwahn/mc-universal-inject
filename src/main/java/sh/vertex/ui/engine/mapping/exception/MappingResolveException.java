package sh.vertex.ui.engine.mapping.exception;

import sh.vertex.ui.engine.structure.Proxy;

import java.lang.reflect.Method;

public class MappingResolveException extends IllegalStateException {

    public MappingResolveException(Class<? extends Proxy> proxy, Throwable cause) {
        super(String.format("Failed to resolve class mapping for proxy %s", proxy.getName()), cause);
    }

    public MappingResolveException(Class<? extends Proxy> proxy, Method method, Throwable cause) {
        super(String.format("Failed to resolve method mapping for proxy %s with method %s", proxy.getName(), method.getName()), cause);
    }

    public MappingResolveException(Class<? extends Proxy> proxy, Method method) {
        this(proxy, method, null);
    }

    public MappingResolveException(Class<? extends Proxy> proxy) {
        this(proxy, (Throwable) null);
    }
}
