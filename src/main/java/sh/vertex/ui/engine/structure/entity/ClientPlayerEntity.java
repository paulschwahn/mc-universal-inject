package sh.vertex.ui.engine.structure.entity;

import sh.vertex.ui.engine.detour.DetourLocation;
import sh.vertex.ui.engine.detour.annotation.DetourHook;
import sh.vertex.ui.engine.detour.impl.ChatMessageDetour;
import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.structure.Minecraft;

@MappingClue(method = DiscoveryMethod.FLOAT, isFieldOf = Minecraft.class, floatConstants = 0.006666667f)
public interface ClientPlayerEntity extends Entity {

    @DetourHook(detour = ChatMessageDetour.class, location = DetourLocation.FIRST)
    @MethodGenerator(value = PopulationMethod.METHOD_BY_DESCRIPTOR, opcodes = {
            ALOAD,
            ALOAD,
            ACONST_NULL,
            INVOKEVIRTUAL
    })
    void chat(String message);
}
