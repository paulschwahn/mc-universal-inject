package sh.vertex.ui.engine.mapping.discovery.mappings;

import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.gui.Window;

public class GuiDiscoverer extends MappingDiscoverer {

    public GuiDiscoverer() {
        this.discover(Window.class);
    }
}
