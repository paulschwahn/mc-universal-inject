package sh.vertex.ui.engine.detour.annotation;

import sh.vertex.ui.engine.detour.Detour;
import sh.vertex.ui.engine.detour.DetourLocation;
import sh.vertex.util.JVMUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DetourHook {

    Class<? extends Detour> detour();
    DetourLocation location() default DetourLocation.CUSTOM;

    // Custom flags
    int[] signature() default {};
    int offset() default 0;
    JVMUtil.SearchTechnique technique() default JVMUtil.SearchTechnique.LAST;
}
