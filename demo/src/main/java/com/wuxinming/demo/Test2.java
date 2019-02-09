package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDApi;

@CRUDApi
public class Test2 {
    private String id;
    private String name2;

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
