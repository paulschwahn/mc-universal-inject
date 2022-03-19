package sh.vertex.ui.engine.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.proxy.providers.HeaderProvider;
import sh.vertex.ui.engine.proxy.providers.ReferenceProvider;
import sh.vertex.ui.engine.structure.Proxy;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dynamic proxy class generator given a simple interface
 */
public class ProxyGenerator implements Opcodes {

    private final ProxyClassLoader proxyClassLoader;
    private final List<ProxyProvider> generatorProviders;

    public ProxyGenerator() {
        this.proxyClassLoader = new ProxyClassLoader(new URL[0], ClassLoader.getSystemClassLoader(), true);
        Thread.currentThread().setContextClassLoader(this.proxyClassLoader);

        List<ProxyProvider> providers = new ArrayList<>();
        Set<ProxyProvider> visited = new HashSet<>();
        providers.add(new HeaderProvider());
        providers.add(new ReferenceProvider());

        this.generatorProviders = new ArrayList<>();
        providers.forEach(provider -> this.resolveDependency(provider, providers, visited));
    }

    private void resolveDependency(ProxyProvider provider, List<ProxyProvider> lookup, Set<ProxyProvider> visited) {
        if (!visited.contains(provider)) {
            visited.add(provider);

            if (provider.getClass().isAnnotationPresent(DependsOn.class)) {
                DependsOn depends = provider.getClass().getAnnotation(DependsOn.class);
                for (Class<? extends ProxyProvider> dependency : depends.value()) {
                    ProxyProvider dep = lookup.stream()
                            .filter(d -> d.getClass() == dependency)
                            .findFirst().orElseThrow(() -> new IllegalStateException("Found dependency for uninitialized class " + dependency.getSimpleName()));

                    this.resolveDependency(dep, lookup, visited);
                }
            }

            this.generatorProviders.add(provider);
        } else if (!this.generatorProviders.contains(provider))
            throw new IllegalStateException("Cyclic proxy provider dependency found for " + provider.getClass().getSimpleName());
    }

    public ProxyClassLoader getProxyClassLoader() {
        return proxyClassLoader;
    }

    /**
     * Generates the proxy class dynamically using all provided {@link ProxyProvider} classes
     *
     * @param proxy Proxy interface without instance
     * @return Proxy class
     */
    public Class<?> proxy(Class<? extends Proxy> proxy) {
        Mapping map = UniversalClient.getInstance().getMappingService().findMappingsByProxy(proxy);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        this.generatorProviders.forEach(provider -> provider.provide(proxy, cw, map));
        cw.visitEnd();
        Class<?> result = this.getProxyClassLoader().defineGlobally(cw.toByteArray());
        map.setProxyImplementation(result);
        return result;
    }
}
