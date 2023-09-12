package com.atguigu.yygh.common.helper;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 这是一个 JWT（JSON Web Token）辅助类 JwtHelper，实现了生成和解析 JWT 的功能。
 * @author SIYU
 */
public class JwtHelper {

    /**
     * 过期时间
     */
    private static long tokenExpiration = 24 * 60 * 60 * 1000; //JWT 的过期时间，单位为毫秒。
    /**
     * 签名密钥
     */
    private static String tokenSignKey = "123456"; //JWT 的签名密钥。

    /**
     * 根据参数生成token
     * @code 指定的用户ID和用户名生成token的方法。userId 参数表示用户的唯一标识，userName 参数表示用户名。该方法返回生成的token字符串。
     * @param userId   用户ID
     * @param userName 用户名
     * @return 生成的token字符串
     */
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    /**
     * 根据token字符串得到用户ID
     * @code 给定的token字符串解析得到用户ID的方法。token 参数表示要解析的token字符串。该方法返回解析出来的用户ID。
     * @param token token字符串
     * @return 用户ID
     *
     */
    public static Long getUserId(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    /**
     * 根据token字符串得到用户名
     * @code 根据给定的token字符串解析得到用户名的方法。token 参数表示要解析的token字符串。该方法返回解析出来的用户名
     * @param token token字符串
     * @return 用户名
     */
    public static String getUserName(String token) {
        if (StringUtils.isEmpty(token)) {
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("userName");
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "lucy");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}
