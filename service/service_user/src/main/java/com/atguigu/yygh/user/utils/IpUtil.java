package com.atguigu.yygh.user.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 获取当前ip的工具类
 */
public class IpUtil {
    /**
     * 示未知IP地址
     */
    private static final String UNKNOWN = "unknown";
    /**
     * 本地主机IP地址
     */
    private static final String LOCALHOST = "127.0.0.1";
    /**
     * 多个IP地址之间的分隔符
     */
    private static final String SEPARATOR = ",";

    /**
     * 获取客户端真实IP地址
     *
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        System.out.println(request);
        String ipAddress;
        try {
            // 从请求头中获取 x-forwarded-for 字段的值作为客户端IP地址
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // 若 x-forwarded-for 字段的值为null、空字符串或"unknown"，则从请求头中获取 Proxy-Client-IP 字段的值
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // 若 Proxy-Client-IP 字段的值为null、空字符串或"unknown"，则从请求头中获取 WL-Proxy-Client-IP 字段的值
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // 若 WL-Proxy-Client-IP 字段的值为null、空字符串或"unknown"，则从请求中获取远程主机的IP地址
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    // 若远程主机IP地址为本地主机地址，则获取本机的IP地址
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    /**
     * IpUtil 类用于获取客户端请求的真实IP地址。
     *
     * UNKNOWN、LOCALHOST 和 SEPARATOR 是常量，分别用于表示未知IP地址、本地主机IP地址和多个IP地址之间的分隔符。
     *
     * getIpAddr() 方法接收一个 HttpServletRequest 对象，并返回客户端的真实IP地址。
     *
     * 方法内部首先尝试从请求头中获取 x-forwarded-for 字段的值作为客户端IP地址。
     *
     * 如果获取到的IP地址为null、空字符串或"unknown"，则尝试从请求头中获取 Proxy-Client-IP 字段的值。
     *
     * 如果获取到的IP地址仍为null、空字符串或"unknown"，则尝试从请求头中获取 WL-Proxy-Client-IP 字段的值。
     *
     * 如果获取到的IP地址仍为null、空字符串或"unknown"，则从请求中获取远程主机的IP地址。
     *
     * 如果获取到的IP地址是本地主机地址（"127.0.0.1"），则获取本机的IP地址。
     *
     * 对于通过多个代理的情况，从获取到的IP地址中提取第一个IP作为客户端的真实IP地址。
     *
     * 若在获取过程中发生异常，则将IP地址设置为空字符串。
     *
     * 最后，方法返回客户端的真实IP地址。
     */
}

