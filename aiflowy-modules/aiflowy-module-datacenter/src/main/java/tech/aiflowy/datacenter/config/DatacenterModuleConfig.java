package tech.aiflowy.datacenter.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("tech.aiflowy.datacenter.mapper")
public class DatacenterModuleConfig {

    public DatacenterModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-datacenter");
    }
}
