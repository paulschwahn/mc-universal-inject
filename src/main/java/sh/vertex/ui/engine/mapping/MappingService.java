package sh.vertex.ui.engine.mapping;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sh.vertex.ui.engine.mapping.discovery.MappingDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.mappings.BaseDiscoverer;
import sh.vertex.ui.engine.mapping.discovery.mappings.GuiDiscoverer;
import sh.vertex.ui.engine.mapping.exception.MappingMissingException;
import sh.vertex.ui.engine.structure.Proxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MappingService {

    private static final Logger logger = LogManager.getLogger();

    @Getter
    private final List<Mapping> mappings;

    public MappingService() {
        this.mappings = new ArrayList<>();
    }

    public void discoverAll() {
        this.discoverUsing(new BaseDiscoverer()); // Minecraft.class, Main.class
        this.discoverUsing(new GuiDiscoverer()); // MainWindow.class

        logger.info("Discovered {} total mappings", mappings.size());
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
