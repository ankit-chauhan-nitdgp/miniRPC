package projects.ankit.demoServerStreaming;

import projects.ankit.constants.Ports;
import projects.ankit.core.MethodRegistry;
import projects.ankit.server.ServerStreamingServer;
import projects.ankit.util.AutoRegistrar;

public class ServerStreamingServerApp {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                MethodRegistry registry = new MethodRegistry();
                // auto-registering services through annotations
                AutoRegistrar.registerAnnotatedServices(registry, "projects.ankit");
                new ServerStreamingServer(Ports.ServerStreamingPort, registry).start();
            } catch (Exception e) {
                System.out.println("Unable to start rcp server :"+e);
            }
        }).start();
    }
}
