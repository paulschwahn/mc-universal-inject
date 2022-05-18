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
 * Generates all specified field setters for a specific proxy
 *
 * <p><b>Requirements:</b></p>
 * <ul>
 *     <li>Method has to have exactly one argument</li>
 *     <li>Return type has to be void</li>
 * </ul>
 *
 * <p>Generates opcode representing a setter:</p>
 * <pre>
 *     {@code
 *          L1:
 *              aload0
 *              getfield [Proxy] instance[ProxyName] L[Proxy];
 *              (if setting a proxied value)    invokevirtual [Proxy] get[ProxyName]Instance
 *              putfield [Internal] [internalFieldName] [internalFieldDescriptor]
 *          L2:
 *              return
 *     }
 * </pre>
 * <p>Example for generating a setter:</p>
 * <pre>
 *     {@code
 *          @MethodGenerator(PopulationMethod.FIELD)
 *          void setTitle(String title);
 *     }
 * </pre>
 *
 * @see sh.vertex.ui.engine.mapping.discovery.methods.FieldDiscoverer
 * @author Paul Schwahn
 * @since 21.03.2022
 */
@DependsOn(ReferenceProvider.class)
public class FieldSetterProvider extends ProxyProvider {

    @Override
    public void provide(Mapping mapping, ClassWriter cw) {
        String instanceName = "instance" + mapping.getProxy().getSimpleName();
        String referenceDescriptor = "L" + mapping.getInternalName() + ";";

        mapping.getMappedFields().forEach((pm /* ^= ProxyMethod */, internal) -> {
            if (pm.getParameterCount() == 1 && pm.getReturnType() == void.class) {
                InsnList insn = new InsnList();
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, pm.getName(), Type.getMethodDescriptor(pm), null, null);

                insn.add(new LabelNode());
                insn.add(new VarInsnNode(ALOAD, 0));
                insn.add(new FieldInsnNode(GETFIELD, getProxiedName(mapping.getProxy()), instanceName, referenceDescriptor));
                if (Proxy.class.isAssignableFrom(pm.getParameterTypes()[0])) {
                    Mapping param = UniversalClient.getInstance().getMappingService().findMappingsByProxy(pm.getParameterTypes()[0].asSubclass(Proxy.class));
                    insn.add(new MethodInsnNode(INVOKEVIRTUAL, getProxiedName(param.getProxy()), "get" + param.getProxy().getSimpleName() + "Instance", Type.getMethodDescriptor(Type.getType(param.getInternalClass()))));
                }
                insn.add(new FieldInsnNode(PUTFIELD, mapping.getInternalName(), internal.getName(), Type.getDescriptor(internal.getType())));
                insn.add(new LabelNode());
                insn.add(new InsnNode(RETURN));
                insn.accept(mv);
                mv.visitMaxs(0, 0);
            }
        });
    }
}
