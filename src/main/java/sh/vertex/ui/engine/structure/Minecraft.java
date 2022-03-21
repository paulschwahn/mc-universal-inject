package sh.vertex.ui.engine.structure;

import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.structure.gui.Window;
import sh.vertex.ui.engine.structure.util.Session;

public interface Minecraft extends Proxy {

    @MethodGenerator(PopulationMethod.METHOD_BY_DESCRIPTOR)
    Window getWindow();

    @MethodGenerator(PopulationMethod.METHOD_BY_DESCRIPTOR)
    Session getSession();
}
