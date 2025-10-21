package projects.ankit.annotationprocessor;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationScanner {

    public static <T extends Annotation> List<Class<?>> findAnnotatedClasses(String basePackage, Class<T> annotationClass) {
        List<Class<?>> result = new ArrayList<>();
        try {
            String path = basePackage.replace('.', '/');
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL resource = loader.getResource(path);
            if (resource == null) return result;

            File directory = new File(resource.toURI());
            scanRecursive(basePackage, directory, annotationClass, result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan package: " + basePackage, e);
        }
        return result;
    }

    private static <T extends Annotation> void scanRecursive(
            String basePackage, File directory, Class<T> annotationClass, List<Class<?>> result) {

        if (!directory.exists() || !directory.isDirectory()) return;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                // recursively calls all folders
                scanRecursive(basePackage + "." + file.getName(), file, annotationClass, result);
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotationClass)) {
                        result.add(clazz);
                    }
                } catch (Throwable ignored) {
                    // skip unloadable classes
                }
            }
        }
    }
}