/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface Cfg {
    public String value() default "";

    public String splitter() default ";";

    public boolean ignore() default 0;

    public String prefix() default "";

    public boolean parametrize() default 0;
}

