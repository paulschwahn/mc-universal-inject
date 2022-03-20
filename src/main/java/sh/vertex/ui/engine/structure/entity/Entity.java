package sh.vertex.ui.engine.structure.entity;

import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.structure.Proxy;

@MappingClue(method = DiscoveryMethod.OLDEST_ANCESTOR, oldestAncestor = ClientPlayerEntity.class)
public interface Entity extends Proxy {
}
