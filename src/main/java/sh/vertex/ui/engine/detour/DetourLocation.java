package sh.vertex.ui.engine.detour;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import sh.vertex.util.JVMUtil;

public enum DetourLocation {
    FIRST,
    LAST,
    BEFORE_RETURN,
    CUSTOM;

    public void insert(MethodNode node, DetourManager.DetourInfo hook, InsnList insn) {
        switch (this) {
            case FIRST -> node.instructions.insert(insn);
            case LAST -> node.instructions.add(insn);
            case CUSTOM -> node.instructions.insertBefore(
                    JVMUtil.findInsn(node.instructions, hook.meta().technique(), hook.meta().offset(), hook.meta().signature()),
                    insn
            );
        }
    }
}
