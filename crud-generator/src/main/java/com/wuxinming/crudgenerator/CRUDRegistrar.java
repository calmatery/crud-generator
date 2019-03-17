package com.wuxinming.crudgenerator;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.asm.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

public class CRUDRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware,Opcodes {

    MapperFactoryBean<Object> mapperFactoryBean =new MapperFactoryBean<Object>();
    private static final String DAO_SUFFIX = "DAO";
    private static final String SERVICE_SUFFIX = "ServiceImpl";
    private static final String CONTROLLER_SUFFIX = "ControllerImpl";

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }

    public static BeanDefinitionRegistry registry;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.registry = registry;
        Map<String,String> types = generatorExistClasses(registry);

        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(CRUDScan.class.getName()));
        if(annoAttrs==null)
            return;

        CRUDClassPathScanner scanner = new CRUDClassPathScanner(registry);
        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (String basePackage : basePackages) {
            Set<BeanDefinitionHolder> bdhs = scanner.doScan(new String[]{basePackage});
            for (BeanDefinitionHolder bdh : bdhs) {
                String className = bdh.getBeanDefinition().getBeanClassName();

                if(!types.containsKey(className+DAO_SUFFIX))
                    daoRegistry(registry, bdh);

                if(!types.containsKey(className+SERVICE_SUFFIX))
                    serviceRegistry(registry,bdh);

                if(!types.containsKey(className+CONTROLLER_SUFFIX))
                    controllerRegistry(registry,bdh,basePackage);
                else{
                    BeanDefinition beanDefinition = registry.getBeanDefinition(types.get(className+CONTROLLER_SUFFIX));
                    beanDefinition.setAttribute("basePackage",basePackage);
                }
            }
        }
    }

    private Map<String,String> generatorExistClasses(BeanDefinitionRegistry registry) {
        Map<String,String> types = new HashMap<>();
        for (String name : registry.getBeanDefinitionNames()) {
            BeanDefinition bd = registry.getBeanDefinition(name);
            if(bd.getBeanClassName()!=null){
                if(bd.getBeanClassName().equals("org.mybatis.spring.mapper.MapperFactoryBean"))    {
                    String metaDataClass=((ScannedGenericBeanDefinition)bd).getMetadata().getClassName();
                    if(metaDataClass!=null){
                        types.put(metaDataClass,name);
                    }
                }
                else {
                    types.put(bd.getBeanClassName(),name);
                }
            }
        }
        return types;
    }

    private void daoRegistry(BeanDefinitionRegistry registry, BeanDefinitionHolder bdh) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(mapperFactoryBean.getClass());
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(generatorDAOClass(bdh.getBeanDefinition().getBeanClassName()));
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(bdh.getBeanDefinition().getBeanClassName()+DAO_SUFFIX,beanDefinition);
    }

    private void serviceRegistry(BeanDefinitionRegistry registry, BeanDefinitionHolder bdh) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        Class serviceClass = generatorServiceClass(bdh.getBeanDefinition().getBeanClassName());
        beanDefinition.setBeanClass(serviceClass);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(bdh.getBeanDefinition().getBeanClassName()+SERVICE_SUFFIX,beanDefinition);
    }

    private void controllerRegistry(BeanDefinitionRegistry registry, BeanDefinitionHolder bdh,String basePackage) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setAttribute("basePackage",basePackage);

        Class controllerClass = generatorControllerClass(bdh.getBeanDefinition().getBeanClassName(),CRUDControllerImpl.MODULE_PATH);
        beanDefinition.setBeanClass(controllerClass);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(bdh.getBeanDefinition().getBeanClassName()+CONTROLLER_SUFFIX,beanDefinition);
    }

    private Class generatorDAOClass(String className){
        String slashClassName = className.replace(".","/");
        String slashBaseMapperCN = "com/baomidou/mybatisplus/core/mapper/BaseMapper";
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_6,
                ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                slashClassName+DAO_SUFFIX,
                "Ljava/lang/Object;L"+slashBaseMapperCN+"<L"+slashClassName+";>;",
                "java/lang/Object", new String[] { slashBaseMapperCN });
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        ChildClassLoader classLoader = new ChildClassLoader();
        Class daoClass = classLoader.defineClass(className+DAO_SUFFIX,bytes);
        return daoClass;
    }

    private Class generatorServiceClass(String className){
        String slashClassName = className.replace(".","/");
        String slashServiceCN = "com/wuxinming/crudgenerator/CRUDServiceImpl";
        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;
        classWriter.visit(V1_6,
                ACC_PUBLIC | ACC_SUPER,
                slashClassName+SERVICE_SUFFIX,
                "L"+slashServiceCN+"<L"+slashClassName+";>;",
                slashServiceCN,
                null);

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(3, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, slashServiceCN, "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", "L"+slashClassName+"ServiceImpl;", null, label0, label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        classWriter.visitEnd();
        ChildClassLoader classLoader = new ChildClassLoader();
        Class serviceClass = classLoader.defineClass(className+SERVICE_SUFFIX,classWriter.toByteArray());
        return serviceClass;
    }

    private Class generatorControllerClass(String className,String modulePath){
        String slashClassName = className.replace(".","/");
        String slashControllerCN = "com/wuxinming/crudgenerator/CRUDControllerImpl";
        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_6,
                ACC_PUBLIC | ACC_SUPER,
                slashClassName+CONTROLLER_SUFFIX,
                "L"+slashControllerCN+"<L"+
                        slashClassName+";>;",
                slashControllerCN,
                null);

        {
            annotationVisitor0 = classWriter.visitAnnotation("Lorg/springframework/web/bind/annotation/RestController;", true);
            annotationVisitor0.visitEnd();
        }
        {
            annotationVisitor0 = classWriter.visitAnnotation("Lorg/springframework/web/bind/annotation/RequestMapping;", true);
            {
                AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("value");
                annotationVisitor1.visit(null, modulePath);
                annotationVisitor1.visitEnd();
            }
            annotationVisitor0.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(8, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, slashControllerCN, "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L"+slashControllerCN+";", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

    ChildClassLoader classLoader = new ChildClassLoader();
        Class controllerClass = classLoader.defineClass(className+CONTROLLER_SUFFIX,classWriter.toByteArray());
        return controllerClass;
    }

}
