package sh.vertex.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sh.vertex.ui.engine.mapping.Mapping;
import sh.vertex.ui.engine.structure.Minecraft;

import java.lang.reflect.Method;

public class EntryPoint {

    private static final Logger logger = LogManager.getLogger();
    private static Minecraft theMinecraft;

    /**
     * Equivalent to the Minecraft.getMinecraft() method within mcp
     *
     * @return The resolved minecraft instance
     */
    public static Minecraft getMinecraft() {
        if (theMinecraft == null) {
            try {
                Mapping mapping = UniversalClient.getInstance().getMappingService().findMappingsByProxy(Minecraft.class);
                Class<?> proxy = mapping.getProxyImplementation();
                Method getMinecraft = mapping.getCustomMappings().get("getMinecraft");
                Object instance = getMinecraft.invoke(null);
                theMinecraft = (Minecraft) proxy.getDeclaredConstructor(mapping.getInternalClass()).newInstance(instance);
            } catch (ReflectiveOperationException e) {
                logger.error("Failed to enter entrypoint", e);
            }
        }

        return theMinecraft;
    }
}
