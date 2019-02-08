package com.wuxinming.CURDGenerator;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.asm.ClassWriter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

public class CURDRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MapperFactoryBean<Object> mapperFactoryBean =new MapperFactoryBean<Object>();
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

        beanDefinition.setBeanClass(mapperFactoryBean.getClass());
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(generatorClass());
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        registry.registerBeanDefinition("tttt",beanDefinition);

        System.out.println(registry);
    }

    static int V1_6 = 0 << 16 | 50;
    static int ACC_PUBLIC = 0x0001; // class, field, method
    static int ACC_INTERFACE = 0x0200; // class
    static int ACC_ABSTRACT = 0x0400; // class, method

    private Class generatorClass(){
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_6, ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE, "pkg/ITestCus", "Ljava/lang/Object;Lcom/baomidou/mybatisplus/core/mapper/BaseMapper<Lcom/jimuwangluo/Test2;>;", "java/lang/Object", new String[] { "com/baomidou/mybatisplus/core/mapper/BaseMapper" });
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        ChildClassLoader classLoader = new ChildClassLoader();
        Class cla = classLoader.defineClass("pkg.ITestCus",bytes);
        return cla;
    }
}
