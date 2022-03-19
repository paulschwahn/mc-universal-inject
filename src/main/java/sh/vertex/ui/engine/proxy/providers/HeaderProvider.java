package sh.vertex.ui.engine.proxy.providers;

import org.objectweb.asm.ClassWriter;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.proxy.ProxyProvider;
import sh.vertex.ui.engine.structure.Proxy;

/**
 * Generates the class header with information like:
 *  1. Class-Version
 *  2. Possible Super Classes
 *  3. Proxy Metadata
 */
public class HeaderProvider extends ProxyProvider {

    @Override
    public void provide(Class<? extends Proxy> proxy, ClassWriter cw, Mapping mapping) {
        String interfaceName = proxy.getName().replace('.', '/');

        if (hasExtends(proxy)) {
            // Generate with extends as super-class
            Class<? extends Proxy> extendsProxy = proxy.getInterfaces()[0].asSubclass(Proxy.class);
            cw.visit(V17, ACC_PUBLIC | ACC_SUPER, getProxiedName(proxy), null, getProxiedName(extendsProxy), new String[]{interfaceName});
        } else {
            // Generate without Proxy extends
            cw.visit(V17, ACC_PUBLIC | ACC_SUPER, getProxiedName(proxy), null, "java/lang/Object", new String[]{interfaceName});
        }
    }
}
