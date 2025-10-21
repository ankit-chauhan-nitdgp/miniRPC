package projects.ankit.util;

import projects.ankit.annotationprocessor.AnnotationScanner;
import projects.ankit.core.MethodRegistry;
import projects.ankit.services.RpcService;

import java.util.List;

public class AutoRegistrar {

    public static void registerAnnotatedServices(MethodRegistry registry, String basePackage) {
        List<Class<?>> annotated = AnnotationScanner.findAnnotatedClasses(basePackage, RpcService.class);

        for (Class<?> clazz : annotated) {
            try {
                RpcService annotation = clazz.getAnnotation(RpcService.class);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                String name = annotation.name().isEmpty() ? clazz.getSimpleName() : annotation.name();
                registry.register(name, instance);
                System.out.println("Auto-registered RPC service: " + name);
            } catch (Exception e) {
                System.out.println("Failed to register: " + clazz.getName());
                throw new RuntimeException("Failed to register: " + clazz.getName(), e);
            }
        }
    }
}
