package sh.vertex.ui.engine.mapping.discovery.classes;

import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.ClassDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.Proxy;

public class OldestAncestorDiscoverer extends ClassDiscoverer {

    @Override
    public Class<?> findUsingClues(MappingDiscoverer discoverer, Class<? extends Proxy> proxy, MappingClue clues) {
        Mapping ancestorProxy = discoverer.findMappingsByProxy(clues.oldestAncestor());
        Class<?> oldestAncestor = ancestorProxy.getInternalClass();
        while (true) {
            Class<?> superClass = oldestAncestor.getSuperclass();
            if (superClass == Object.class) {
                return oldestAncestor;
            }
            oldestAncestor = superClass;
        }
    }
}
