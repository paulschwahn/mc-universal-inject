package sh.vertex.ui.engine.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sh.vertex.ui.UniversalClient;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProxyClassLoader extends URLClassLoader {

    private static final Logger logger = LogManager.getLogger();

    private final boolean dumpClasses;

    public ProxyClassLoader(URL[] urls, ClassLoader parent, boolean dumpClasses) {
        super(urls, parent);
        this.dumpClasses = dumpClasses;
    }

    public Class<?> defineGlobally(byte[] bytecode) {
        try {
            Method m1 = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            m1.setAccessible(true);
            Class<?> out = (Class<?>) m1.invoke(this.getClass().getClassLoader(), bytecode, 0, bytecode.length);

            if (dumpClasses) {
                Path outDir = UniversalClient.getInstance().getClientPath().resolve("proxy-out");
                Files.createDirectories(outDir);
                Files.write(outDir.resolve(out.getSimpleName() + ".class"), bytecode);
            }

            return out;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
