package sh.vertex.ui.engine.mapping.exception;

import sh.vertex.ui.engine.structure.Proxy;

public class MappingMissingException extends IllegalStateException {

    public MappingMissingException(Class<? extends Proxy> proxy) {
        super(String.format("Missing mapping for proxy %s", proxy.getName()));
    }
}
