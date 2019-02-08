package com.wuxinming.CURDGenerator;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Configuration
@CURDScan("com.jimuwangluo")
@AutoConfigureAfter(MybatisPlusAutoConfiguration.class)
public class CURDScanConfiguration {
    public CURDScanConfiguration(){
        System.out.println("aaaaa");
    }

}
