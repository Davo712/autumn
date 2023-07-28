package org.wntr.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndPoint {
    String type() default "get";

    boolean needRC() default false;

    String redirectPath() default "";

    String mappingPath();


}
