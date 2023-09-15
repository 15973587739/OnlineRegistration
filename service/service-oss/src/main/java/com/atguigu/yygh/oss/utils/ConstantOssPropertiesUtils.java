package com.atguigu.yygh.oss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 操作阿里云OSS的工具类
 * @author SIYU
 */
@Component //将这个类标记为Spring组件，以便能够进行依赖注入和其他相关的容器管理操作。
public class ConstantOssPropertiesUtils implements InitializingBean {

    // 阿里云OSS的Endpoint
    @Value("${aliyun.oss.endpoint}") //用于从配置文件中获取值并注入到对应的属性中。
    private String endpoint;

    // 阿里云OSS的Access Key ID
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    // 阿里云OSS的Access Key Secret
    @Value("${aliyun.oss.secret}")
    private String secret;

    // 阿里云OSS的存储桶名称
    @Value("${aliyun.oss.bucket}")
    private String bucket;

    // 静态变量，用于存储从配置文件中读取的值
    /**
     * 阿里云OSS的Endpoint
     */
    public static String ENDPOINT;
    /**
     * 阿里云OSS的Access Key ID
     */
    public static String ACCESS_KEY_ID;
    /**
     * 阿里云OSS的Access Key Secret
     */
    public static String SECRECT;
    /**
     * 阿里云OSS的存储桶名称
     */
    public static String BUCKET;

    /**
     * 初始化方法，在对象的属性设置完成后调用
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 将配置文件中的值赋给静态变量
        ENDPOINT = endpoint;
        ACCESS_KEY_ID = accessKeyId;
        SECRECT = secret;
        BUCKET = bucket;
    }
}
