package sh.vertex.ui.engine.mapping.discovery;

import sh.vertex.ui.engine.mapping.PopulationMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodGenerator {

    PopulationMethod value();
}
