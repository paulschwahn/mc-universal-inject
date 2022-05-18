package sh.vertex.ui.injector;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class UniversalInjector {

    public static void main(String[] args) throws Throwable {
        UniversalInjector injector = new UniversalInjector(
            VirtualMachine.list()
                .stream()
                .filter(vmd -> vmd.displayName().startsWith("net.minecraft.client.main.Main"))
                .findFirst().orElseThrow()
        );
        injector.loadAgent();
    }

    private final VirtualMachineDescriptor descriptor;
    private VirtualMachine vm;
    private final File self;

    public UniversalInjector(VirtualMachineDescriptor descriptor) throws IOException, AttachNotSupportedException, URISyntaxException {
        this.self = new File(UniversalInjector.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        this.descriptor = descriptor;
        this.attach();
    }

    private void attach() throws IOException, AttachNotSupportedException {
        this.vm = VirtualMachine.attach(this.descriptor);
    }

    private void loadAgent() throws AgentLoadException, IOException, AgentInitializationException {
        if (this.vm == null) throw new IllegalStateException("Injector not attached");
        this.vm.loadAgent(this.self.getAbsolutePath(), Paths.get(".").toAbsolutePath().toString());
    }
}
