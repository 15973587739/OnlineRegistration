package com.atguigu.yygh.common.utils;

import com.atguigu.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前用户信息工具类
 * @author SIYU
 */
public class AuthContextHolder {

    /**
     * 获取当前用户ID
     * @param request HTTP请求对象
     * @return 当前用户的ID
     */
    public static Long getUserId(HttpServletRequest request) {
        // 从header获取token
        String token = request.getHeader("token");
        // 使用JWT从token中获取userID
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    /**
     * 获取当前用户名称
     * @param request HTTP请求对象
     * @return 当前用户的名称
     */
    public static String getUserName(HttpServletRequest request) {
        // 从header获取token
        String token = request.getHeader("token");
        // 使用JWT从token中获取用户名
        String userName = JwtHelper.getUserName(token);
        return userName;
    }

}
