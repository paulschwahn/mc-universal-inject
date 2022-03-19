package sh.vertex.ui.engine.proxy.providers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.mapping.PopulationMethod;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.proxy.DependsOn;
import sh.vertex.ui.engine.proxy.ProxyProvider;
import sh.vertex.ui.engine.structure.Proxy;

@DependsOn(HeaderProvider.class)
public class GetterToGetterProvider extends ProxyProvider {

    @Override
    public void provide(Mapping mapping, ClassWriter cw) {
        String instanceName = "instance" + mapping.getProxy().getSimpleName();
        String referenceDescriptor = "L" + mapping.getInternalName() + ";";

        mapping.getMappedMethods().forEach((pm /* ^= ProxyMethod */, internal) -> {
            MethodGenerator gen = pm.getAnnotation(MethodGenerator.class);
            if (gen.value() == PopulationMethod.GETTER_CALL) {
                boolean needsProxy = Proxy.class.isAssignableFrom(pm.getReturnType());
                Class<? extends Proxy> resultProxy = null;

                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, pm.getName(), Type.getMethodDescriptor(pm), null, null);
                mv.visitCode();
                Label returnLabel = new Label();
                mv.visitLabel(returnLabel);

                if (needsProxy) {
                    resultProxy = pm.getReturnType().asSubclass(Proxy.class);
                    mv.visitTypeInsn(NEW, getProxiedName(resultProxy));
                    mv.visitInsn(DUP);
                }

                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, getProxiedName(mapping.getProxy()), instanceName, referenceDescriptor);
                mv.visitMethodInsn(INVOKEVIRTUAL, mapping.getInternalName(), internal.getName(), Type.getMethodDescriptor(internal), false);

                if (needsProxy) {
                    Mapping result = UniversalClient.getInstance().getMappingService().findMappingsByProxy(resultProxy);
                    mv.visitMethodInsn(INVOKESPECIAL, getProxiedName(resultProxy), "<init>", "(L" + result.getInternalName() + ";)V", false);
                }

                mv.visitInsn(Type.getType(pm.getReturnType()).getOpcode(IRETURN));
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        });
    }
}
