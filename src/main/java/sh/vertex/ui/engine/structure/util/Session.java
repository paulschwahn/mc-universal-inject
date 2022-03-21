package sh.vertex.ui.engine.structure.util;

import com.mojang.authlib.GameProfile;
import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.structure.Minecraft;
import sh.vertex.ui.engine.structure.Proxy;

@MappingClue(method = DiscoveryMethod.STRING_LITERAL, isFieldOf = Minecraft.class, literals = "token:")
public interface Session extends Proxy {

    @MethodGenerator(PopulationMethod.METHOD_BY_DESCRIPTOR)
    GameProfile getProfile();
}
