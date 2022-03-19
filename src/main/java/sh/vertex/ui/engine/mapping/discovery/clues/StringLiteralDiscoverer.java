package sh.vertex.ui.engine.mapping.discovery.clues;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import sh.vertex.ui.engine.mapping.discovery.ClueDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.Proxy;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class StringLiteralDiscoverer extends ClueDiscoverer {

    @Override
    public Class<?> findUsingClues(MappingDiscoverer discoverer, Class<? extends Proxy> proxy, MappingClue clues) {
        Class<?> declaringClass = discoverer.findMappingsByProxy(clues.isFieldOf()).getInternalClass();
        String[] literals = clues.literals();

        assert literals.length > 0;

        return Stream.of(declaringClass.getDeclaredFields())
                .map(Field::getType)
                .filter(t -> !t.isPrimitive() && !t.isArray())
                .filter(t -> {
                    ClassNode classNode = JVMUtil.getClassNode(t);

                    for (MethodNode node : classNode.methods) {
                        int streak = 0;

                        for (AbstractInsnNode insn : node.instructions) {
                            if (insn.getType() == AbstractInsnNode.LDC_INSN && insn instanceof LdcInsnNode) {
                                Object valObj = ((LdcInsnNode) insn).cst;
                                if (valObj instanceof String val) {
                                    if (val.equals(literals[streak]))
                                        streak++;
                                    else
                                        streak = 0;

                                    if (streak == literals.length) {
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
