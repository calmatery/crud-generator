package com.wuxinming.CURDGenerator;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CURDRegistrar.class)
public @interface CURDScan {
    String[] value() default{} ;
}
