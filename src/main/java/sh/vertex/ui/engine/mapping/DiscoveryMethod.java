package sh.vertex.ui.engine.mapping;

import sh.vertex.ui.engine.mapping.discovery.ClassDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.classes.FloatDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.classes.OldestAncestorDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.classes.StringLiteralDiscoverer;
import sh.vertex.ui.engine.structure.Proxy;

public enum DiscoveryMethod {

    STRING_LITERAL(new StringLiteralDiscoverer()),
    FLOAT(new FloatDiscoverer()),
    OLDEST_ANCESTOR(new OldestAncestorDiscoverer());

    private final ClassDiscoverer discoverer;

    DiscoveryMethod(ClassDiscoverer discoverer) {
        this.discoverer = discoverer;
    }

    public Class<?> discoverClass(MappingDiscoverer discoverer, Class<? extends Proxy> proxy, MappingClue info) throws Throwable {
        return this.discoverer.findUsingClues(discoverer, proxy, info);
    }
}
