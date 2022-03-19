package sh.vertex.ui.engine.mapping;

import sh.vertex.ui.engine.mapping.discovery.ClueDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.clues.FloatDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.clues.OldestAncestorDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.clues.StringLiteralDiscoverer;

public enum DiscoveryMethod {

    STRING_LITERAL(new StringLiteralDiscoverer()),
    FLOAT(new FloatDiscoverer()),
    OLDEST_ANCESTOR(new OldestAncestorDiscoverer());

    private final ClueDiscoverer discoverer;

    DiscoveryMethod(ClueDiscoverer discoverer) {
        this.discoverer = discoverer;
    }

    public Class<?> discoverClass(MappingClue info) throws Throwable {
        return this.discoverer.findUsingClues(info);
    }
}
