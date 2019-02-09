package com.wuxinming.demo;

import com.wuxinming.crudgenerator.CRUDRequestMappingHandlerMapping;
import com.wuxinming.crudgenerator.CRUDScan;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
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
}
