package sh.vertex.ui.engine.proxy.providers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.proxy.DependsOn;
import sh.vertex.ui.engine.proxy.ProxyProvider;
import sh.vertex.ui.engine.structure.Proxy;

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
            Label returnLabel = new Label();
            mv.visitLabel(returnLabel);

            if (hasReturnType && needsProxy) {
                resultProxy = pm.getReturnType().asSubclass(Proxy.class);
                mv.visitTypeInsn(NEW, getProxiedName(resultProxy));
                mv.visitInsn(DUP);
            }

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, getProxiedName(mapping.getProxy()), instanceName, referenceDescriptor);

            int pos = 1;
            for (int i = 0; i < pm.getParameterCount(); i++) {
                Class<?> param = pm.getParameterTypes()[i];
                mv.visitVarInsn(Type.getType(param).getOpcode(ILOAD), pos);
                pos += Type.getType(param).getSize();
                if (Proxy.class.isAssignableFrom(param)) {
                    Class<? extends Proxy> paramProxyClass = param.asSubclass(Proxy.class);
                    Mapping paramProxy = UniversalClient.getInstance().getMappingService().findMappingsByProxy(paramProxyClass);
                    mv.visitTypeInsn(CHECKCAST, getProxiedName(paramProxyClass));
                    mv.visitMethodInsn(INVOKEVIRTUAL, getProxiedName(paramProxyClass), "get" + paramProxyClass.getSimpleName() + "Instance", "()L" + paramProxy.getInternalName() + ";", false);
                }
            }

            mv.visitMethodInsn(INVOKEVIRTUAL, mapping.getInternalName(), internal.getName(), Type.getMethodDescriptor(internal), false);

            if (hasReturnType && needsProxy) {
                Mapping result = UniversalClient.getInstance().getMappingService().findMappingsByProxy(resultProxy);
                mv.visitMethodInsn(INVOKESPECIAL, getProxiedName(resultProxy), "<init>", "(L" + result.getInternalName() + ";)V", false);
            }

            mv.visitInsn(hasReturnType ? Type.getType(pm.getReturnType()).getOpcode(IRETURN) : RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        });
    }
}
