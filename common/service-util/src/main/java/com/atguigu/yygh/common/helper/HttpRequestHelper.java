package com.atguigu.yygh.common.helper;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.utils.HttpUtil;
import com.atguigu.yygh.common.utils.MD5;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一个 HTTP 请求辅助类 HttpRequestHelper，用于处理 HTTP 请求相关的操作
 * @author SIYU
 */
@Slf4j //使用 Lombok 注解，生成日志记录器。
public class HttpRequestHelper {

    public static void main(String[] args) {
        // 构造请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("d", "4");
        paramMap.put("b", "2");
        paramMap.put("c", "3");
        paramMap.put("a", "1");
        paramMap.put("timestamp", getTimestamp());

        // 打印生成的签名
        log.info(getSign(paramMap, "111111111"));
    }

    /**
     * 将请求参数 Map 类型转换为 Object 类型
     *
     * @param paramMap 请求参数 Map
     * @return 转换后的请求参数 Map
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }

    /**
     * 获取请求数据的签名
     *
     * @param paramMap 请求参数 Map
     * @param signKey  签名密钥
     * @return 请求数据的签名
     */
    public static String getSign(Map<String, Object> paramMap, String signKey) {
        if (paramMap.containsKey("sign")) {
            paramMap.remove("sign");
        }
        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            str.append(param.getValue()).append("|");
        }
        str.append(signKey);

        log.info("加密前：" + str.toString());
        String md5Str = MD5.encrypt(str.toString());
        log.info("加密后：" + md5Str);
        return md5Str;
    }

    /**
     * 签名校验
     *
     * @param paramMap 请求参数 Map
     * @param signKey  签名密钥
     * @return 签名是否匹配
     */
    public static boolean isSignEquals(Map<String, Object> paramMap, String signKey) {
        String sign = (String) paramMap.get("sign");
        String md5Str = MD5.encrypt(signKey);
//        String md5Str = getSign(paramMap, signKey);
        return sign.equals(md5Str);
    }

    /**
     * 获取时间戳
     *
     * @return 当前时间的时间戳
     */
    public static long getTimestamp() {
        return new Date().getTime();
    }

    /**
     * 发送同步请求
     *
     * @param paramMap 请求参数 Map
     * @param url      目标 URL
     * @return 响应结果的 JSON 对象
     */
    public static JSONObject sendRequest(Map<String, Object> paramMap, String url) {
        String result = "";
        try {
            // 构建 POST 请求参数
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                postData.append(param.getKey()).append("=")
                        .append(param.getValue()).append("&");
            }
            log.info(String.format("--> 发送请求：post data %1s", postData));

            byte[] reqData = postData.toString().getBytes("utf-8");
            byte[] respData = HttpUtil.doPost(url, reqData);
            result = new String(respData);
            log.info(String.format("--> 应答结果：result data %1s", result));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return JSONObject.parseObject(result);
    }
}
