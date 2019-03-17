package com.wuxinming.crudgenerator;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CRUDRegistrar.class)
public @interface CRUDScan {
    String[] value() default {};
}
