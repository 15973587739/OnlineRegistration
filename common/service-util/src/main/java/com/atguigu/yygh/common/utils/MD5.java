package com.atguigu.yygh.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author SIYU
 * MD5 加密工具类 MD5，用于对字符串进行 MD5 加密。
 */
public final class MD5 {

    /**
     * 对字符串进行 MD5 加密
     *
     * @param strSrc 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String strSrc) {
        try {
            char hexChars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            byte[] bytes = strSrc.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }
}
