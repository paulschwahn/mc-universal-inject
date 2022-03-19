package sh.vertex.ui.engine.mapping.discovery.mappings;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.Main;
import sh.vertex.ui.engine.structure.Minecraft;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.stream.Stream;

public class BaseDiscoverer extends MappingDiscoverer {

    public BaseDiscoverer() {
        this.discover(Main.class, () -> Class.forName(JVMUtil.getMainClassName()));

        this.discover(Minecraft.class, () -> {
            ClassNode node = JVMUtil.getClassNode(findMappingsByProxy(Main.class).getInternalClass());
            MethodNode main = JVMUtil.getMethod(node, "main", "([Ljava/lang/String;)V");
            assert main != null;
            TypeInsnNode insn = JVMUtil.findInsn(main.instructions, JVMUtil.SearchTechnique.LAST, 0,
                    NEW,
                    DUP,
                    ALOAD,
                    INVOKESPECIAL);
            return Class.forName(Objects.requireNonNull(insn).desc);
        });

        this.discover(Minecraft.class, "getMinecraft", () -> {
            Mapping mapping = findMappingsByProxy(Minecraft.class);
            Class<?> internal = mapping.getInternalClass();
            return Stream.of(internal.getDeclaredMethods()).filter(m -> Modifier.isStatic(m.getModifiers()) && m.getReturnType() == internal).findFirst().orElse(null);
        });
    }
}
