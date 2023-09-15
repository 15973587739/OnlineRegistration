package com.atguigu.yygh.msm.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author SIYU
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean {

    // 阿里云短信服务的区域ID
    @Value("${aliyun.sms.regionId}")
    private String regionId;

    // 阿里云短信服务的访问密钥ID
    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    // 阿里云短信服务的访问密钥
    @Value("${aliyun.sms.secret}")
    private String secret;

    // 静态变量，保存区域ID
    public static String REGION_Id;

    // 静态变量，保存访问密钥ID
    public static String ACCESS_KEY_ID;

    // 静态变量，保存访问密钥
    public static String SECRECT;

    /**
     * 初始化方法，在属性设置完成后自动调用
     *
     * 通过InitializingBean接口，将属性值赋给静态变量，用于后续访问阿里云短信服务。
     *
     * @throws Exception 初始化异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_Id = regionId;
        ACCESS_KEY_ID = accessKeyId;
        SECRECT = secret;
    }
}
