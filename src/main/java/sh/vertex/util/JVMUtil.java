package sh.vertex.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JVMUtil implements Opcodes {

    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, ClassNode> NODE_CACHE = new HashMap<>();

    private JVMUtil() {
    }

    public static String getMainClassName() {
        for (Map.Entry<String, String> entry : System.getenv().entrySet())
            if (entry.getKey().startsWith("JAVA_MAIN_CLASS"))
                return entry.getValue();
        return "net.minecraft.client.main.Main";
    }

    public static ClassNode getClassNode(Class<?> internalClass) {
        String className = Type.getInternalName(internalClass) + ".class";
        if (NODE_CACHE.containsKey(className)) return NODE_CACHE.get(className);
        byte[] classData;
        try {
            InputStream is = JVMUtil.class.getResourceAsStream("/" + className);
            assert is != null;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            classData = buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClassNode classNode = acceptClass(classData);
        NODE_CACHE.put(className, classNode);
        return classNode;
    }

    public static ClassNode acceptClass(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        new ClassReader(bytes).accept(classNode, ClassReader.SKIP_FRAMES);
        return classNode;
    }

    public static MethodNode getMethod(ClassNode node, String name, String desc) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(name) && method.desc.equals(desc))
                return method;
        }
        return null;
    }

    public static Method getMethod(Class<?> node, String desc) {
        for (Method method : node.getDeclaredMethods()) {
            if (Type.getMethodDescriptor(method).equals(desc))
                return method;
        }
        return null;
    }

    /**
     * Finds a AbstractInsnNode based on given opcodes within a InsnList
     *
     * @param list Instruction list
     * @param technique Search technique, search for first or last in {@link InsnList}, compareable to indexOf and lastIndexOf in strings
     * @param nthNode nth insn node in opcodes to return
     * @param opcodes Opcodes to search for {@link Opcodes}
     * @return Combination of nthNode and technique, null if no match was found
     */
    @SuppressWarnings("unchecked")
    public static <T> T findInsn(InsnList list, SearchTechnique technique, int nthNode, int... opcodes) {
        if (opcodes.length == 0) throw new IllegalArgumentException("Need at least 1 opcode, 0 given");
//        if (nthNode >= opcodes.length || nthNode < 0) throw new IllegalArgumentException("Can't get a node ahead or behind opcode size");
//        not quite sure mb there is some use case where custom nthMode would be useful

        AbstractInsnNode match = null;

        for (int i = 0; i < list.size(); i++) {
            AbstractInsnNode insn = list.get(i);
            if (insn.getOpcode() == opcodes[0]) {
                if (i == opcodes.length && opcodes.length == 1) return (T) insn;
                if ((i + opcodes.length) > list.size()) break;

                boolean found = true;

                for (int j = 1; j < opcodes.length; j++) {
                    if (list.get(i+j).getOpcode() != opcodes[j]) {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    match = list.get(i+nthNode);
                    if (technique == SearchTechnique.FIRST) return (T) match;
                }
            }
        }

        return match == null ? null : (T) match;
    }

    public static boolean containsOpcodes(Method method, int[] opcodes) {
        if (opcodes.length == 0) return true;
        var node = getClassNode(method.getDeclaringClass());
        var mn = getMethod(node, method.getName(), Type.getMethodDescriptor(method));
        assert mn != null;
        return findInsn(mn.instructions, SearchTechnique.FIRST, 0, opcodes) != null;
    }

    public static String asmify(String name) {
        return name.replace('.', '/');
    }

    public static int getFirstFreeStackIndex(MethodNode node) {
        var max = 0;
        var isBigInsn = false;
        var currentInsn = node.instructions.getFirst();
        while (currentInsn != null) {
            if (currentInsn instanceof VarInsnNode val) {
                if (val.var > max) {
                    max = val.var;
                    var c = val.getOpcode();
                    isBigInsn = c == FLOAD || c == DLOAD || c == FSTORE || c == DSTORE;
                }
            }
            currentInsn = currentInsn.getNext();
        }
        return max + (isBigInsn ? 2 : 1);
    }

    public enum SearchTechnique {
        FIRST,
        LAST
    }
}
