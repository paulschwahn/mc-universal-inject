package sh.vertex.ui.engine.structure;

import org.objectweb.asm.Opcodes;

public interface Proxy extends Opcodes {

    Object getInternalObject();
}
