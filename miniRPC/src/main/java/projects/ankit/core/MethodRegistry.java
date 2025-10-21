package projects.ankit.core;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class MethodRegistry {
    private final ConcurrentHashMap<String, Object> services = new ConcurrentHashMap<>();

    public void register(String name, Object impl) {
        services.put(name, impl);
    }

    public Object invoke(Request req) throws Exception {
        String method = req.getMethodName();
        String[] parts = method.split("#");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid method format: " + method);

        Object service = services.get(parts[0]);
        if (service == null) throw new IllegalArgumentException("Service not found: " + parts[0]);

        for (Method m : service.getClass().getMethods()) {
            if (m.getName().equals(parts[1])) {
                return m.invoke(service, req.getParams());
            }
        }
        throw new NoSuchMethodException("Method not found: " + parts[1]);
    }
}
