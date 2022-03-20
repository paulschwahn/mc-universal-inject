package sh.vertex.ui.engine.mapping.discovery.mappings;

import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.entity.ClientPlayerEntity;
import sh.vertex.ui.engine.structure.entity.Entity;

public class EntityDiscoverer extends MappingDiscoverer {

    public EntityDiscoverer() {
        this.discover(ClientPlayerEntity.class);
        this.discover(Entity.class);
    }
}
