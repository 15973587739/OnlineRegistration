package com.atguigu.yygh.user.api;


import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.Result;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantWxPropertiesUtils;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信API控制器
 *微信操作的接口
 * 该控制器提供了与微信相关的功能接口。
 */
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 微信扫描后回调方法
     *
     * @param code  微信返回的临时票据code
     * @param state 微信返回的state参数
     * @return 重定向到前端页面
     */
    @GetMapping("callback")
    public String callback(String code,String state) {
        // 第一步 获取临时票据code
        System.out.println("code:"+code);

        // 第二步 使用code、微信id和秘钥请求微信固定地址，得到两个值
        // 使用code、appid以及appsecret换取access_token
        // %s表示占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);
        // 使用HttpClientUtils发起HTTP请求
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accesstokenInfo:"+accesstokenInfo);
            // 从返回字符串中获取openid和access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            // 判断数据库是否存在微信的扫描用户信息
            // 根据openid进行判断
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if(userInfo == null) { // 数据库中不存在微信信息
                // 第三步 使用openid和access_token请求微信地址，得到扫描用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo:"+resultInfo);
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                // 解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");  // 用户昵称
                String headimgurl = resultUserInfoJson.getString("headimgurl");  // 用户头像

                // 添加扫描用户信息到数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }

            // 返回name和token字符串
            Map<String,String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);

            // 判断userInfo是否有手机号，如果手机号为空，返回openid
            // 如果手机号不为空，返回openid值是空字符串
            // 前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }

            // 使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //跳转到前端页面
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+
                    "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 生成微信扫描二维码
     * 生成一个用于微信扫描的二维码，并返回生成的二维码信息。
     *
     * @return 包含生成的二维码信息的Result对象
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(){
        try {
            Map<String,Object> map =new HashMap<>();
            // 设置微信接口秘钥
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            // 设置扫码后需要手动确认的scope为snsapi_login
            map.put("scope","snsapi_login");
            // 获取微信扫码成功后的回调路径
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            // 进行URL编码以确保安全传输
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            // 设置扫码成功后的回调路径
            map.put("redirect_uri",wxOpenRedirectUrl);
            // 可选参数，设置当前时间作为state参数
            map.put("state",System.currentTimeMillis()+"");
            return Result.ok(map);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }

}
