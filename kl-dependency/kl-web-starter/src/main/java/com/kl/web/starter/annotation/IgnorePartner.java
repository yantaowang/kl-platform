package com.kl.web.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnorePartner {
    /**
     * @return true 忽略合作方信息拦截
     */
    boolean isIgnore() default true;
}
