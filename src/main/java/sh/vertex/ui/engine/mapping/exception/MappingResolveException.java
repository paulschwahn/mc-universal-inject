package sh.vertex.ui.engine.mapping.exception;

import sh.vertex.ui.engine.structure.Proxy;

public class MappingResolveException extends IllegalStateException {

    public MappingResolveException(Class<? extends Proxy> proxy, Throwable cause) {
        super(String.format("Failed to resolve class mapping for proxy %s", proxy.getName()), cause);
    }

    public MappingResolveException(Class<? extends Proxy> proxy) {
        this(proxy, null);
    }
}
