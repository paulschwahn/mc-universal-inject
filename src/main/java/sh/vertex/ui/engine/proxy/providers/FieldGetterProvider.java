package sh.vertex.ui.engine.proxy.providers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.proxy.DependsOn;
import sh.vertex.ui.engine.proxy.ProxyProvider;
import sh.vertex.ui.engine.structure.Proxy;
import sh.vertex.util.JVMUtil;

@DependsOn(ReferenceProvider.class)
public class FieldGetterProvider extends ProxyProvider {

    @Override
    public void provide(Mapping mapping, ClassWriter cw) {
        var instanceName = "instance" + mapping.getProxy().getSimpleName();
        var referenceDescriptor = "L" + mapping.getInternalName() + ";";

        mapping.getMappedFields().forEach((pm /* ^= ProxyMethod */, internal) -> {
            if (pm.getParameterCount() == 0 && pm.getReturnType() != void.class) {
                var iType = Type.getType(internal.getType());
                var needsProxy = Proxy.class.isAssignableFrom(pm.getReturnType());
                Class<? extends Proxy> resultProxy = null;

                var insn = new InsnList();
                var mv = cw.visitMethod(ACC_PUBLIC, pm.getName(), Type.getMethodDescriptor(pm), null, null);

                insn.add(new LabelNode());

                if (needsProxy) {
                    resultProxy = pm.getReturnType().asSubclass(Proxy.class);
                    insn.add(new TypeInsnNode(NEW, getProxiedName(resultProxy)));
                    insn.add(new InsnNode(DUP));
                }

                insn.add(new VarInsnNode(ALOAD, 0));
                insn.add(new FieldInsnNode(GETFIELD, getProxiedName(mapping.getProxy()), instanceName, referenceDescriptor));
                insn.add(new FieldInsnNode(GETFIELD, mapping.getInternalName(), internal.getName(), Type.getDescriptor(internal.getType())));

                if (needsProxy) insn.add(new MethodInsnNode(INVOKESPECIAL, getProxiedName(resultProxy), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, iType)));

                insn.add(new InsnNode(iType.getOpcode(IRETURN)));
                insn.accept(mv);
                mv.visitMaxs(0, 0);
            }
        });
    }
}
