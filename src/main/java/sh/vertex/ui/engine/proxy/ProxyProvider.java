package sh.vertex.ui.engine.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.structure.Proxy;

public abstract class ProxyProvider implements Opcodes {

    public abstract void provide(Class<? extends Proxy> proxy, ClassWriter cw, Mapping mapping);

    /**
     * Check if a specific proxy has another proxy attached to it
     *
     * @param proxy To check
     * @return true if parent proxy is unique
     */
    protected boolean hasExtends(Class<? extends Proxy> proxy) {
        return proxy.getInterfaces().length >= 1 && !proxy.getInterfaces()[0].equals(Proxy.class);
    }

    protected String getProxiedName(Class<? extends Proxy> proxy) {
        return "sh/vertex/ui/engine/proxy/proxies/" + proxy.getSimpleName() + "Proxy";
    }
}
