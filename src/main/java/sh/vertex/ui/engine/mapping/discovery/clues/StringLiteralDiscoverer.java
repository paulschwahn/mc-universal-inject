package sh.vertex.ui.engine.mapping.discovery.clues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.*;
import sh.vertex.ui.engine.mapping.discovery.ClueDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MappingClue;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.structure.Proxy;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class StringLiteralDiscoverer extends ClueDiscoverer {

    private static final Logger logger = LogManager.getLogger();

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
                            if (insn.getType() == AbstractInsnNode.LDC_INSN && insn instanceof LdcInsnNode ldc) {
                                Object valObj = ldc.cst;
                                if (valObj instanceof String val) {
                                    if (val.equals(literals[streak]))
                                        streak++;
                                    else
                                        streak = 0;

                                    if (streak == literals.length) {
                                        return true;
                                    }
                                }
                            } else if (insn.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN && insn instanceof InvokeDynamicInsnNode indy) {
                                if (indy.name.equals("makeConcatWithConstants") && indy.bsmArgs.length == 1) {
                                    Object bsmArg = indy.bsmArgs[0];
                                    if (bsmArg instanceof String arg) {
                                        for (String str : arg.split("\u0001")) {
                                            if (str.equals(literals[streak]))
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
                        }
                    }

                    return false;
                }).findFirst().orElse(null);
    }
}
