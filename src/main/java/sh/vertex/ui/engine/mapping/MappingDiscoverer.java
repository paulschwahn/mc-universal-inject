package sh.vertex.ui.engine.mapping;

import org.objectweb.asm.Opcodes;
import sh.vertex.ui.UniversalClient;
import sh.vertex.ui.engine.mapping.exception.MappingMissingException;
import sh.vertex.ui.engine.mapping.exception.MappingResolveException;
import sh.vertex.ui.engine.structure.Proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class MappingDiscoverer implements Opcodes {

    private final List<Mapping> discoveries;

    public MappingDiscoverer() {
        this.discoveries = new ArrayList<>();
    }

    public List<Mapping> getDiscoveries() {
        return this.discoveries;
    }

    public void discover(Class<? extends Proxy> proxy, ClassProcessor... processors) {
        Mapping mapping = new Mapping(proxy);
        for (ClassProcessor processor : processors) {
            try {
                mapping.setInternalClass(processor.process());
            } catch (Throwable e) {
                throw new MappingResolveException(proxy, e);
            }
        }

        this.discoveries.add(mapping);
    }

    protected void discover(Class<? extends Proxy> proxy, String name, MethodProcessor processor) {
        Mapping mapping = this.findMappingsByProxy(proxy);
        try {
            mapping.getCustomMappings().put(name, processor.process());
        } catch (Throwable e) {
            throw new MappingResolveException(proxy, e);
        }
    }

    public Mapping findMappingsByProxy(Class<? extends Proxy> proxy) {
        MappingService service = UniversalClient.getInstance().getMappingService();
        return Stream.concat(service.getMappings().stream(), this.discoveries.stream()).filter(p -> p.getProxy().equals(proxy)).findFirst().orElseThrow(() -> new MappingMissingException(proxy));
    }

    protected interface ClassProcessor {
        Class<?> process() throws ClassNotFoundException, IOException;
    }

    protected interface MethodProcessor {
        Method process();
    }
}
