package sh.vertex.ui.engine.proxy.providers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.proxy.DependsOn;
import sh.vertex.ui.engine.proxy.ProxyProvider;
import sh.vertex.ui.engine.structure.Proxy;


/**
 * <p>Provides all kinds of methods to a given proxy, every getter, setter or regular method call,
 * any return type or parameter will get properly converted by this Provider</p>
 *
 * <p>Automatically resolves proxy classes,</p>
 *
 * @author Paul Schwahn
 * @since 20.03.2022
 */
@DependsOn(ReferenceProvider.class)
public class MethodProvider extends ProxyProvider {

    @Override
    public void provide(Mapping mapping, ClassWriter cw) {
        String instanceName = "instance" + mapping.getProxy().getSimpleName();
        String referenceDescriptor = "L" + mapping.getInternalName() + ";";

        mapping.getMappedMethods().forEach((pm /* ^= ProxyMethod */, internal) -> {
            boolean needsProxy = Proxy.class.isAssignableFrom(pm.getReturnType());
            boolean hasReturnType = pm.getReturnType() != void.class;
            Class<? extends Proxy> resultProxy = null;

            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, pm.getName(), Type.getMethodDescriptor(pm), null, null);
            mv.visitCode();
            InsnList insn = new InsnList();
            insn.add(new LabelNode());

            if (hasReturnType && needsProxy) {
                resultProxy = pm.getReturnType().asSubclass(Proxy.class);
                insn.add(new TypeInsnNode(NEW, getProxiedName(resultProxy)));
                insn.add(new InsnNode(DUP));
            }

            insn.add(new VarInsnNode(ALOAD, 0));
            insn.add(new FieldInsnNode(GETFIELD, getProxiedName(mapping.getProxy()), instanceName, referenceDescriptor));

            int pos = 1;
            for (int i = 0; i < pm.getParameterCount(); i++) {
                Class<?> param = pm.getParameterTypes()[i];
                insn.add(new VarInsnNode(Type.getType(param).getOpcode(ILOAD), pos));
                pos += Type.getType(param).getSize();
                if (Proxy.class.isAssignableFrom(param)) {
                    Class<? extends Proxy> paramProxyClass = param.asSubclass(Proxy.class);
                    Mapping paramProxy = UniversalClient.getInstance().getMappingService().findMappingsByProxy(paramProxyClass);
                    insn.add(new TypeInsnNode(CHECKCAST, getProxiedName(paramProxyClass)));
                    insn.add(new MethodInsnNode(INVOKEVIRTUAL, getProxiedName(paramProxyClass), "get" + paramProxyClass.getSimpleName() + "Instance", "()L" + paramProxy.getInternalName() + ";"));
                }
            }

            insn.add(new MethodInsnNode(INVOKEVIRTUAL, mapping.getInternalName(), internal.getName(), Type.getMethodDescriptor(internal)));

            if (hasReturnType && needsProxy) {
                Mapping result = UniversalClient.getInstance().getMappingService().findMappingsByProxy(resultProxy);
                insn.add(new MethodInsnNode(INVOKESPECIAL, getProxiedName(resultProxy), "<init>", "(L" + result.getInternalName() + ";)V"));
            }

            insn.add(new InsnNode(hasReturnType ? Type.getType(pm.getReturnType()).getOpcode(IRETURN) : RETURN));
            insn.accept(mv);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        });
    }
}
