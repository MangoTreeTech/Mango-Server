package com.mango.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 常量类，配置全局常量
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "constant")
public class Constant {
    public String dir = "./image";
}
