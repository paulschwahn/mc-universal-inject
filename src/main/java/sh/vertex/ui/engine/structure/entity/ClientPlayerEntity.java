package sh.vertex.ui.engine.structure.entity;

import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.structure.Minecraft;

@MappingClue(method = DiscoveryMethod.FLOAT, isFieldOf = Minecraft.class, floatConstants = 0.006666667f)
public interface ClientPlayerEntity extends Entity {
}
