package sh.vertex.ui.engine.structure.gui;

import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.structure.Minecraft;
import sh.vertex.ui.engine.structure.Proxy;

@MappingClue(method = DiscoveryMethod.STRING_LITERAL, isFieldOf = Minecraft.class, literals = {"http", "https"})
public interface Screen extends Proxy {
}
