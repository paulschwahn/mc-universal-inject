package sh.vertex.ui.engine.detour;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import sh.vertex.ui.engine.detour.annotation.DetourHook;
import sh.vertex.ui.engine.detour.annotation.DetourHooks;
import sh.vertex.ui.engine.mapping.Mapping;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DetourManager implements ClassFileTransformer {

    private static final Logger logger = LogManager.getLogger();

    private final Instrumentation instrumentation;
    private final List<MethodData> registeredDetours;
    private final List<DetourInfo> detourHooks;

    public DetourManager(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.instrumentation.addTransformer(this, true);
        this.registeredDetours = new ArrayList<>();
        this.detourHooks = new ArrayList<>();
    }

    public void register(Object classObj) {
        Stream.of(classObj.getClass().getDeclaredMethods())
                .filter(m -> m.getParameterCount() == 1 && m.getReturnType() == void.class)
                .filter(m -> Detour.class.isAssignableFrom(m.getParameterTypes()[0]))
                .forEach(m -> this.registeredDetours.add(new MethodData(m, classObj)));
    }

    public void unregister(Object classObj) {
        this.registeredDetours.removeIf(data -> data.classReference == classObj);
    }

    public void call(Detour detour) {
        this.registeredDetours.stream()
                .filter(d -> d.matches(detour))
                .forEach(d -> d.call(detour));
    }

    public void hookDetours(List<Mapping> mappings) throws UnmodifiableClassException {
        List<Class<?>> detouredClasses = new ArrayList<>();
        for (Mapping mapping : mappings) {
            boolean hasHooks = false;

            for (Method m : mapping.getMappedMethods().keySet()) {
                List<DetourHook> hooks = new ArrayList<>();
                if (m.isAnnotationPresent(DetourHook.class))
                    hooks.add(m.getAnnotation(DetourHook.class));
                else if (m.isAnnotationPresent(DetourHooks.class))
                    hooks.addAll(Arrays.asList(m.getAnnotation(DetourHooks.class).value()));

                if (!hasHooks) hasHooks = !hooks.isEmpty();

                hooks.forEach(hook -> this.detourHooks.add(new DetourInfo(mapping, mapping.getMappedMethods().get(m), hook)));
            }

            if (hasHooks) detouredClasses.add(mapping.getInternalClass());
        }

        this.instrumentation.retransformClasses(detouredClasses.toArray(new Class<?>[0]));
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        List<DetourInfo> hooks = this.detourHooks.stream()
                .filter(d -> d.mapping.getInternalName().equals(className)).toList();

        if (hooks.size() > 0) {
            Mapping mapping = hooks.stream().findAny().get().mapping();
            logger.info("Hooking {} detours within {} ({})", hooks.size(), className, mapping.getProxy().getSimpleName());

            ClassReader reader = new ClassReader(classfileBuffer);
            DetourNode visitor = new DetourNode(hooks);
            reader.accept(visitor, 0);

            try {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                visitor.accept(cw);
                return cw.toByteArray();
            } catch (Throwable t) {
                logger.error(t);
            }
        }

        return null;
    }

    private record MethodData(Method detourMethod, Object classReference) {
        private boolean matches(Detour detour) {
            return this.detourMethod.getParameterTypes()[0] == detour.getClass();
        }

        @SneakyThrows
        private void call(Detour detour) {
            this.detourMethod.invoke(this.classReference, detour);
        }
    }

    public record DetourInfo(Mapping mapping, Method internalMethod, DetourHook meta) {}
}
