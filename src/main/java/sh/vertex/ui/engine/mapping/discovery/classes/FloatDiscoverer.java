package sh.vertex.ui.engine.mapping.discovery.classes;

import org.objectweb.asm.tree.*;
import sh.vertex.ui.engine.mapping.discovery.ClassDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.Proxy;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class FloatDiscoverer extends ClassDiscoverer {

    @Override
    public Class<?> findUsingClues(MappingDiscoverer discoverer, Class<? extends Proxy> proxy, MappingClue clues) {
        Class<?> declaringClass = discoverer.findMappingsByProxy(clues.isFieldOf()).getInternalClass();
        float[] constants = clues.floatConstants();

        assert constants.length > 0;

        return Stream.of(declaringClass.getDeclaredFields())
                .map(Field::getType)
                .filter(t -> !t.isPrimitive() && !t.isArray())
                .filter(t -> {
                    ClassNode classNode = JVMUtil.getClassNode(t);

                    for (MethodNode node : classNode.methods) {
                        int streak = 0;

                        for (AbstractInsnNode insn : node.instructions) {
                            if (insn.getType() == AbstractInsnNode.LDC_INSN && insn instanceof LdcInsnNode ldc) {
                                Object valObj = ldc.cst;
                                if (valObj instanceof Float val) {
                                    if (val.equals(constants[streak]))
                                        streak++;
                                    else
                                        streak = 0;

                                    if (streak == constants.length) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }

                    return false;
                }).findFirst().orElse(null);
    }
}
