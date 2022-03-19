package sh.vertex.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class MethodVisitorNode extends ClassNode implements Opcodes {

    private static final Logger logger = LogManager.getLogger();

    public MethodVisitorNode() {
        super(ASM9);
    }

    @Override
    public void accept(ClassVisitor classVisitor) {
        for (MethodNode n : this.methods) {
            if (n.name.equals("setAccessible")) {
                InsnList override = new InsnList();
                override.add(new VarInsnNode(ALOAD, 0));
                override.add(new VarInsnNode(ILOAD, 1));
                override.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/reflect/AccessibleObject", "setAccessible0", "(Z)Z"));
                override.add(new InsnNode(POP));
                override.add(new InsnNode(RETURN));
                n.instructions.insert(override);
            }
        }
        super.accept(classVisitor);
    }
}
