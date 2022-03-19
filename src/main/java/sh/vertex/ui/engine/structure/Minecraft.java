package sh.vertex.ui.engine.structure;

import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.structure.gui.Window;

public interface Minecraft extends Proxy {

    @MethodGenerator(PopulationMethod.GETTER_CALL)
    Window getWindow();
}
