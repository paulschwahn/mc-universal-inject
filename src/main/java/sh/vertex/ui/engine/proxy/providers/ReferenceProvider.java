package sh.vertex.ui.engine.proxy.providers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.proxy.DependsOn;
import sh.vertex.ui.engine.proxy.ProxyProvider;
import sh.vertex.ui.engine.structure.Proxy;

@DependsOn(HeaderProvider.class)
public class ReferenceProvider extends ProxyProvider {

    @Override
    public void provide(Mapping mapping, ClassWriter cw) {
        Class<? extends Proxy> proxy = mapping.getProxy();
        String instanceName = "instance" + proxy.getSimpleName();
        String referenceDescriptor = "L" + mapping.getInternalName() + ";";

        // Generate field to hold reference
        FieldVisitor fv = cw.visitField(ACC_PRIVATE, instanceName, referenceDescriptor, null, null);
        fv.visitEnd();

        // Generate constructor with reference as parameter
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + referenceDescriptor + ")V", null, null);
        {
            mv.visitCode();

            Label constructor = new Label();
            mv.visitLabel(constructor);

            if (hasExtends(proxy)) {
                Class<? extends Proxy> superProxy = proxy.getInterfaces()[0].asSubclass(Proxy.class);
                Mapping superClass = UniversalClient.getInstance().getMappingService().findMappingsByProxy(superProxy);
                mv.visitVarInsn(ALOAD, 0); // this
                mv.visitVarInsn(ALOAD, 1); // constructor argument (reference)

                // this.super(reference);
                mv.visitMethodInsn(INVOKESPECIAL, getProxiedName(superProxy), "<init>", "(L" + superClass.getInternalName() + ";)V", false);
            } else {
                mv.visitVarInsn(ALOAD, 0); // this

                // this.super();
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            }

            Label storeInstanceLabel = new Label();
            mv.visitLabel(storeInstanceLabel);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            // this.instance<proxy> = reference;
            mv.visitFieldInsn(PUTFIELD, getProxiedName(proxy), instanceName, referenceDescriptor);

            Label returnLabel = new Label();
            mv.visitLabel(returnLabel);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // Create getter for specific reference
        populateGetter(cw.visitMethod(ACC_PUBLIC, "get" + proxy.getSimpleName() + "Instance", "()" + referenceDescriptor, null, null), proxy, referenceDescriptor, instanceName);

        // Create getter for general reference (getInternalObject, implemented in Proxy.java)
        populateGetter(cw.visitMethod(ACC_PUBLIC, "getInternalObject", "()Ljava/lang/Object;", null, null), proxy, referenceDescriptor, instanceName);
    }

    private void populateGetter(MethodVisitor mv, Class<? extends Proxy> proxy, String referenceDescriptor, String instanceName) {
        mv.visitCode();
        Label returnLabel = new Label();
        mv.visitLabel(returnLabel);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, getProxiedName(proxy), instanceName, referenceDescriptor);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
