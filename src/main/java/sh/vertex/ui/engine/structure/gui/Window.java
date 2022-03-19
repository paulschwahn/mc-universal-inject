package sh.vertex.ui.engine.structure.gui;

import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.structure.Minecraft;
import sh.vertex.ui.engine.structure.Proxy;

@MappingClue(method = DiscoveryMethod.STRING_LITERAL, isFieldOf = Minecraft.class, literals = {"Pre startup"})
public interface Window extends Proxy {

    @MethodGenerator(PopulationMethod.GETTER_CALL)
    long getHandle();
}
