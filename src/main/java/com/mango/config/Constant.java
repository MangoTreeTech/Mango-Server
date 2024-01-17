package com.mango.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 常量类，配置全局常量
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "constant")
public class Constant {
    //图片存储默认位置
    public String dir = "./image";

    //腾讯云密钥
    public String secretId = "AKIDHUaWyIdprjN1qeImCSaggu0vcEiDrOsJ";

    public String secretKey = "b19sRQI5khhaN2nbUUWcdP0Xt2Vnuxyr";

    public String bucketName = "textdemo-1323245674";

    //mango最新版本
    public String LATEST_VERSION = "1.0.0";
}
