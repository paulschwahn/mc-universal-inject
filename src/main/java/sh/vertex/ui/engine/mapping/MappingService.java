package sh.vertex.ui.engine.mapping;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.MethodGenerator;
import sh.vertex.ui.engine.mapping.discovery.mappings.BaseDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.mappings.EntityDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.mappings.GuiDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.mappings.UtilDiscoverer;
import sh.vertex.ui.engine.mapping.exception.MappingMissingException;
import sh.vertex.ui.engine.mapping.exception.MappingResolveException;
import sh.vertex.ui.engine.structure.Proxy;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingService {

    private static final Logger logger = LogManager.getLogger();

    @Getter
    private final List<Mapping> mappings;

    public MappingService() {
        this.mappings = new ArrayList<>();
    }

    public void discoverAll() {
        this.discoverUsing(new BaseDiscoverer()); // Minecraft.class, Main.class
        this.discoverUsing(new GuiDiscoverer()); // MainWindow.class, Screen.class
        this.discoverUsing(new UtilDiscoverer()); // Session.class
        this.discoverUsing(new EntityDiscoverer()); // Entity.class, ClientPlayerEntity.class
        logger.info("Discovered {} total mappings", mappings.size());

        this.mappings.forEach(mapping -> Stream.of(mapping.getProxy().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(MethodGenerator.class)).forEach(m -> {
            MethodGenerator gen = m.getAnnotation(MethodGenerator.class);
            try {
                AccessibleObject match = gen.value().tryDiscover(mapping, m, gen);
                if (match == null) throw new MappingResolveException(mapping.getProxy(), m);
                if (match instanceof Method method) {
                    mapping.getMappedMethods().put(m, method);
                    logger.info("Discovered method {}#{} as {}#{} using {}", mapping.getProxy().getSimpleName(), m.getName(), mapping.getInternalClass().getSimpleName(), method.getName(), gen.value().getName());
                } else if (match instanceof Field field) {
                    mapping.getMappedFields().put(m, field);
                    logger.info("Discovered field {}#{} as {}#{} using {}", mapping.getProxy().getSimpleName(), m.getName(), mapping.getInternalClass().getSimpleName(), field.getName(), gen.value().getName());
                }
            } catch (Throwable t) {
                throw new MappingResolveException(mapping.getProxy(), m, t);
            }
        }));
    }

    private void discoverUsing(MappingDiscoverer discoverer) {
        this.mappings.addAll(discoverer.getDiscoveries());
    }

    public Mapping findMappingsByProxy(Class<? extends Proxy> proxy) {
        return this.mappings.stream().filter(p -> p.getProxy().equals(proxy)).findFirst().orElseThrow(() -> new MappingMissingException(proxy));
    }

    public void dump(Path path) throws IOException {
        Files.write(path, mappings.stream().map(m -> m.getProxy().getName() + " -> " + m.getInternalClass().getName()).collect(Collectors.toList()));
    }
}
