package projects.ankit.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String name() default ""; // optional custom name
}
