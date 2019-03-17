package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDRequestMappingHandlerMapping;
import com.wuxinming.crudgenerator.CRUDScan;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@MapperScan("com.wuxinming.demo")
@CRUDScan("com.wuxinming.demo")
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class CRUDScanConfiguration extends WebMvcConfigurationSupport{
    public CRUDScanConfiguration(){
    }

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new CRUDRequestMappingHandlerMapping();
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }


    public static void main(String[] args) {
//        System.out.println(generatorModuleURL("com.wuxinming.demo.test.test1.TestClass", "com.wuxinming.demo"));
    }



}
