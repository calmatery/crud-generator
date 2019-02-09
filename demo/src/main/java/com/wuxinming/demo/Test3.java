package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDApi;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collection;

@CRUDApi
public class Test3 {
    private String id;
    private String name2;
//    Collection
//    SimpleUrlHandlerMapping
//    RequestMappingHandlerMapping

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

}
