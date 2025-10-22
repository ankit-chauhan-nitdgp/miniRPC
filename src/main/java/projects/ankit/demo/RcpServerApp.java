package projects.ankit.demo;

import projects.ankit.core.MethodRegistry;
import projects.ankit.server.RpcServer;
import projects.ankit.server.StreamServer;
import projects.ankit.util.AutoRegistrar;

public class RcpServerApp {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                MethodRegistry registry = new MethodRegistry();
                // auto-registering services through annotations
                AutoRegistrar.registerAnnotatedServices(registry, "projects.ankit");
                new RpcServer(9000, registry).start();
            } catch (Exception e) {
                System.out.println("Unable to start rcp server :"+e);
            }
        }).start();
    }
}