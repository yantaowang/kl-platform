package com.kl.starter.shardings.annotation;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DS("sharding")
public @interface DSShardings {
}