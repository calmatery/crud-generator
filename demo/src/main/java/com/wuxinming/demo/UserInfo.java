package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDApi;

@CRUDApi
public class UserInfo {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
