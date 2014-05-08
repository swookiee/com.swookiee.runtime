package com.swookiee.runtime.util.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OSGiExport {

    ServiceProperties[] properties() default {};

    public @interface ServiceProperties {
        public String key();
        public String[] value();
    }
}
