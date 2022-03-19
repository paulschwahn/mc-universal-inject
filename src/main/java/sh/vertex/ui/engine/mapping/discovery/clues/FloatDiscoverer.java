package sh.vertex.ui.engine.mapping.discovery.clues;

import sh.vertex.ui.engine.mapping.discovery.ClueDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.Proxy;

public class FloatDiscoverer extends ClueDiscoverer {

    @Override
    public Class<?> findUsingClues(MappingDiscoverer discoverer, Class<? extends Proxy> proxy, MappingClue clues) {
        return null;
    }
}
