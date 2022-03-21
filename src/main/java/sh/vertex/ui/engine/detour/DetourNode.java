package sh.vertex.ui.engine.detour;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.detour.annotation.DetourFieldInjection;
import sh.vertex.ui.engine.detour.impl.SetTitleDetour;
import sh.vertex.ui.engine.detour.types.CancelableDetour;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.structure.Proxy;
import sh.vertex.util.JVMUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DetourNode extends ClassNode implements Opcodes {

    private final List<DetourManager.DetourInfo> hooks;

    public DetourNode(List<DetourManager.DetourInfo> hooks) {
        super(ASM9);
        this.hooks = hooks;
    }

    private InsnList generateHook(MethodNode node, DetourManager.DetourInfo hook) {
        InsnList i = new InsnList();

        String universalClientName = JVMUtil.asmify(UniversalClient.class.getName());
        String instanceGetterName = Stream.of(UniversalClient.class.getDeclaredMethods()).filter(f -> Modifier.isStatic(f.getModifiers()) && f.getReturnType() == UniversalClient.class && f.getParameterCount() == 0).map(Method::getName).findFirst().orElse("getInstance");
        String detourManagerGetterName = Stream.of(UniversalClient.class.getDeclaredMethods()).filter(f -> f.getReturnType() == DetourManager.class && f.getParameterCount() == 0).map(Method::getName).findFirst().orElse("getDetourManager");
        String detourManagerName = JVMUtil.asmify(DetourManager.class.getName());
        String detourName = JVMUtil.asmify(hook.meta().detour().getName());
        String callName = Stream.of(DetourManager.class.getDeclaredMethods()).filter(f -> f.getReturnType() == void.class && f.getParameterCount() == 1 && f.getParameterTypes()[0] == Detour.class).map(Method::getName).findFirst().orElse("call");

        List<Method> injectionSetter = Stream.of(hook.meta().detour().getDeclaredMethods()).filter(h -> h.getParameterCount() == 1 && h.isAnnotationPresent(DetourFieldInjection.class)).toList();
        List<Method> injectionGetter = Stream.of(hook.meta().detour().getDeclaredMethods()).filter(h -> h.getParameterCount() == 0 && h.getReturnType() != void.class && h.isAnnotationPresent(DetourFieldInjection.class)).toList();

        boolean cancelable = CancelableDetour.class.isAssignableFrom(hook.meta().detour());
        boolean needsField = cancelable || !injectionSetter.isEmpty() || !injectionGetter.isEmpty();

        if (needsField) {
            LabelNode stackFrameLabel = new LabelNode();
            int storeIndex = JVMUtil.getFirstFreeStackIndex(node);

            i.add(new LabelNode());
            i.add(new TypeInsnNode(NEW, detourName));
            i.add(new InsnNode(DUP));
            i.add(new MethodInsnNode(INVOKESPECIAL, detourName, "<init>", "()V"));
            i.add(new VarInsnNode(ASTORE, storeIndex));

            for (Method m : injectionSetter) {
                var injector = m.getAnnotation(DetourFieldInjection.class);
                var param = m.getParameterTypes()[0];
                var isProxy = Proxy.class.isAssignableFrom(param);
                Mapping mapping = null;

                i.add(new LabelNode());
                i.add(new VarInsnNode(ALOAD, storeIndex));
                if (isProxy) {
                    mapping = UniversalClient.getInstance().getMappingService().findMappingsByProxy(param.asSubclass(Proxy.class));
                    i.add(new TypeInsnNode(NEW, mapping.getGeneratedProxy()));
                    i.add(new InsnNode(DUP));
                }
                i.add(new VarInsnNode(Type.getType(param).getOpcode(ILOAD), injector.value()));
                if (isProxy) i.add(new MethodInsnNode(INVOKESPECIAL, mapping.getGeneratedProxy(), "<init>", "(L" + mapping.getInternalName() + ";)V"));
                i.add(new MethodInsnNode(INVOKEVIRTUAL, detourName, m.getName(), Type.getMethodDescriptor(m)));
            }

            i.add(new LabelNode());
            i.add(new MethodInsnNode(INVOKESTATIC, universalClientName, instanceGetterName, "()L" + universalClientName + ";"));
            i.add(new MethodInsnNode(INVOKEVIRTUAL, universalClientName, detourManagerGetterName, "()L" + detourManagerName + ";"));
            i.add(new VarInsnNode(ALOAD, storeIndex));
            i.add(new MethodInsnNode(INVOKEVIRTUAL, detourManagerName, callName, Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Detour.class))));

            for (Method m : injectionGetter) {
                var injector = m.getAnnotation(DetourFieldInjection.class);
                var ret = m.getReturnType();
                var isProxy = Proxy.class.isAssignableFrom(ret);

                i.add(new LabelNode());
                i.add(new VarInsnNode(ALOAD, storeIndex));
                i.add(new MethodInsnNode(INVOKEVIRTUAL, detourName, m.getName(), Type.getMethodDescriptor(m)));
                if (isProxy) {
                    var mapping = UniversalClient.getInstance().getMappingService().findMappingsByProxy(ret.asSubclass(Proxy.class));
                    var rp = mapping.getGeneratedProxy();
                    i.add(new TypeInsnNode(CHECKCAST, rp));
                    i.add(new MethodInsnNode(INVOKEVIRTUAL, rp, "get" + mapping.getProxy().getSimpleName() + "Instance", "()L" + mapping.getInternalName() + ";"));
                }
                i.add(new VarInsnNode(Type.getType(m.getReturnType()).getOpcode(ISTORE), injector.value()));
            }


            if (cancelable) {
                i.add(new LabelNode());
                i.add(new VarInsnNode(ALOAD, storeIndex));
                i.add(new MethodInsnNode(INVOKEVIRTUAL, detourName, Objects.requireNonNull(JVMUtil.getMethod(CancelableDetour.class, Type.getMethodDescriptor(Type.BOOLEAN_TYPE))).getName(), "()Z"));

                i.add(new LabelNode());
                i.add(new JumpInsnNode(IFEQ, stackFrameLabel));
                i.add(new InsnNode(RETURN));

                i.add(stackFrameLabel);
                i.add(new FrameNode(F_FULL, 2, new Object[] {this.name, detourName}, 0, new Object[0]));
            }
        } else {
            i.add(new LabelNode());
            i.add(new MethodInsnNode(INVOKESTATIC, universalClientName, instanceGetterName, "()L" + universalClientName + ";"));
            i.add(new MethodInsnNode(INVOKEVIRTUAL, universalClientName, detourManagerGetterName, "()L" + detourManagerName + ";"));
            i.add(new TypeInsnNode(NEW, detourName));
            i.add(new InsnNode(DUP));
            i.add(new MethodInsnNode(INVOKESPECIAL, detourName, "<init>", "()V"));
            i.add(new MethodInsnNode(INVOKEVIRTUAL, detourManagerName, callName, Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Detour.class))));
        }

        return i;
    }

    @Override
    public void accept(ClassVisitor classVisitor) {
        for (MethodNode node : this.methods) {
            List<DetourManager.DetourInfo> methodHooks = hooks.stream()
                    .filter(d -> d.internalMethod().getName().equals(node.name))
                    .filter(d -> Type.getMethodDescriptor(d.internalMethod()).equals(node.desc)).toList();

            methodHooks.forEach(d -> d.meta().location().insert(node, d, generateHook(node, d)));
        }
        super.accept(classVisitor);
    }
}
