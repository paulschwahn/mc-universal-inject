package sh.vertex.ui.engine.mapping.discovery.mappings;

import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.util.Session;

public class UtilDiscoverer extends MappingDiscoverer {

    public UtilDiscoverer() {
        this.discover(Session.class);
    }
}
