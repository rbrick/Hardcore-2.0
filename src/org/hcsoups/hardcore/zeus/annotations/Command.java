package org.hcsoups.hardcore.zeus.annotations;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    @Nonnull String name() default "";

    String[] aliases() default {};

    String desc() default "";

    String usage() default "";

    String permission() default "";

    String permissionMsg() default "";

    int minArgs() default 0;

    int maxArgs() default -1;
}
