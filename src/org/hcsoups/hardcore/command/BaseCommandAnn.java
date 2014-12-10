package org.hcsoups.hardcore.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Ryan on 11/30/2014
 * <p/>
 * Project: HCSoups
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BaseCommandAnn {

   String name();

   String[] aliases();

   String permission() default "";

   String usage();

   int minArgs() default 0;

   int maxArgs() default -1;

}
