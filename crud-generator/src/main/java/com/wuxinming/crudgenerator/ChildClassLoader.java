package com.wuxinming.crudgenerator;

public class ChildClassLoader extends ClassLoader{
    public Class<?> defineClass(String name, byte[] bytes) {
        Class<?> clazz = super.defineClass(name, bytes, 0, bytes.length);
        return clazz;
    }
}
