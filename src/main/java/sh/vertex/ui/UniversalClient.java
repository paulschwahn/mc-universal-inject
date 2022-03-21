package sh.vertex.ui;

import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import sh.vertex.ui.engine.detour.DetourManager;
import sh.vertex.ui.engine.detour.impl.SetTitleDetour;
import sh.vertex.ui.engine.mapping.MappingService;
import sh.vertex.ui.engine.proxy.ProxyGenerator;
import sh.vertex.util.MethodVisitorNode;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

@Data
public class UniversalClient {

    private static final Logger logger = LogManager.getLogger();

    private static UniversalClient instance;

    public static UniversalClient getInstance() {
        return instance;
    }

    public static void agentmain(String args, Instrumentation inst) throws Throwable {
        new UniversalClient(inst, args);
    }

    private final Instrumentation instrumentation;
    private final MappingService mappingService;
    private final ProxyGenerator proxyGenerator;
    private final DetourManager detourManager;
    private final Path clientPath;

    public UniversalClient(Instrumentation instrumentation, String arguments) throws Throwable {
        instance = this;
        this.clientPath = Paths.get(arguments);
        this.instrumentation = instrumentation;

        this.patchMethod();

        this.mappingService = new MappingService();
        this.mappingService.discoverAll();
        this.mappingService.dump(this.clientPath.resolve("mappings.txt"));

        this.proxyGenerator = new ProxyGenerator();
        this.proxyGenerator.buildProxies(this.mappingService.getMappings());

        this.detourManager = new DetourManager(this.instrumentation);
        this.detourManager.hookDetours(this.mappingService.getMappings());

        logger.info("Initialized UniversalClient");
        logger.info(EntryPoint.getMinecraft().getInternalObject());
        logger.info("Window ID: {}", EntryPoint.getMinecraft().getWindow().getHandle());
        logger.info("Username: {}", EntryPoint.getMinecraft().getSession().getProfile().getName());

        this.detourManager.register(this);
    }

    private void patchMethod() throws Throwable {
        this.instrumentation.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className.equals("java/lang/reflect/Method")) {
                    try {
                        ClassReader reader = new ClassReader(classfileBuffer);
                        MethodVisitorNode visitor = new MethodVisitorNode();
                        reader.accept(visitor, 0);
                        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                        visitor.accept(cw);
                        logger.info("Overwrote access permissions on java.lang.reflect.Method");
                        return cw.toByteArray();
                    } catch (Throwable e) {
                        logger.error("Failed to patch java.lang.reflect.Method", e);
                    }
                }
                return null;
            }
        }, true);
        this.instrumentation.retransformClasses(Method.class);
    }
}
