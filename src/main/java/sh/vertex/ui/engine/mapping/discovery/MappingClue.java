package sh.vertex.ui.engine.mapping.discovery;

import sh.vertex.ui.engine.mapping.DiscoveryMethod;
import sh.vertex.ui.engine.structure.Proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappingClue {

    DiscoveryMethod method();

    Class<? extends Proxy> isFieldOf() default Proxy.class;
    String[] literals() default {};
    float[] floatConstants() default {};

    Class<? extends Proxy> oldestAncestor() default Proxy.class;

}
