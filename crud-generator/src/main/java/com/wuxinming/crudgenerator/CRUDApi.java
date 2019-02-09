package com.wuxinming.crudgenerator;

public @interface CRUDApi {
    boolean dao() default true;
    boolean service() default true;
    boolean controller() default true;
}
