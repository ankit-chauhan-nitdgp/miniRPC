package projects.ankit.demoClientStreaming;

import projects.ankit.constants.Ports;
import projects.ankit.core.MethodRegistry;
import projects.ankit.server.ClientStreamingServer;
import projects.ankit.util.AutoRegistrar;

public class ClientStreamingServerApp {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                MethodRegistry registry = new MethodRegistry();
                // auto-registering services through annotations
                AutoRegistrar.registerAnnotatedServices(registry, "projects.ankit");
                new ClientStreamingServer(Ports.ClientStreamingPort, registry).start();
            } catch (Exception e) {
                System.out.println("Unable to start rcp server :"+e);
            }
        }).start();
    }

}
