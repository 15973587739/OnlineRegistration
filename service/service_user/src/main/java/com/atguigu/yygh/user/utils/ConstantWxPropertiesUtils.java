package com.atguigu.yygh.user.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author SIYU
 */
@Component
public class ConstantWxPropertiesUtils implements InitializingBean {

    @Value("${wx.open.app_id}")
    private String appiid;

    @Value("${wx.open.redirect_url}")
    private String url;

    @Value("${wx.open.app_secret}")
    private String secret;

    @Value("${yygh.baseUrl}")
    private String yyghBaseUrl;

    /**
     * 微信开放平台应用密钥
     */
    public static String WX_OPEN_APP_SECRET;

    /**
     * 微信开放平台应用 ID
     */
    public static String WX_OPEN_APP_ID;


    /**
     * 微信开放平台重定向 URL
     */
    public static String WX_OPEN_REDIRECT_URL;


    /**
     * 预约挂号系统基础 URL
     */
    public static String YYGH_BASE_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化静态变量
        WX_OPEN_APP_ID = appiid;
        WX_OPEN_REDIRECT_URL = url;
        WX_OPEN_APP_SECRET = secret;
        YYGH_BASE_URL = yyghBaseUrl;
    }

    /**
     * 上述代码定义了一个名为 ConstantWxPropertiesUtils 的类，该类被注解为 @Component，表示它是一个由 Spring 管理的组件。类实现了 InitializingBean 接口，该接口提供了 afterPropertiesSet 方法用于在类初始化后执行初始化逻辑。
     *
     * 代码中使用了 @Value 注解，通过读取配置文件中的属性值，并将其注入到相应的私有变量中。这些私有变量对应了不同的微信开放平台和预约挂号系统的属性。
     *
     * 在类中还定义了一些静态变量，用于存储上述属性值。这些静态变量可以被其他组件或类直接访问，以便获取相应的属性值。
     *
     * 在 afterPropertiesSet 方法中，将读取到的属性值赋给对应的静态变量，完成初始化操作。
     */
}
