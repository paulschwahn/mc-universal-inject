package sh.vertex.ui.engine.structure.gui;

import sh.vertex.ui.engine.detour.DetourLocation;
import sh.vertex.ui.engine.detour.annotation.DetourHook;
import sh.vertex.ui.engine.detour.impl.SetTitleDetour;
import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.structure.Minecraft;
import sh.vertex.ui.engine.structure.Proxy;

@MappingClue(method = DiscoveryMethod.STRING_LITERAL, isFieldOf = Minecraft.class, literals = {"Pre startup"})
public interface Window extends Proxy {

    @MethodGenerator(PopulationMethod.METHOD_BY_DESCRIPTOR)
    long getHandle();

    @MethodGenerator(value = PopulationMethod.METHOD_BY_DESCRIPTOR, opcodes = {
            ALOAD, GETFIELD,    // this.handle
            ALOAD,              // title
            INVOKESTATIC        // GLFW.glfwSetWindowTitle
    })
    @DetourHook(detour = SetTitleDetour.class, location = DetourLocation.FIRST)
    void setTitle(String title);
}
