package com.atguigu.yygh.msm.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.util.ConstantPropertiesUtils;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SIYU
 */
@Service
public class MsmServiceImpl implements MsmService {


    /**
     * 发送手机验证码
     *
     * 通过指定手机号和验证码，发送手机验证码。
     *
     * @param phone 手机号码
     * @param code  验证码
     * @return 是否发送成功，发送成功返回true，否则返回false
     */
    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            throw new YyghException(ResultCodeEnum.SERVICE_ERROR);
        }

        //整合阿里云短信服务
        //设置相关参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "我的尚医通学习网站");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_463160733");
        //验证码  使用json格式   {"code":"1234"}
        Map<String, Object> param = new HashMap();
        param.put("code", code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));
        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * MQ发送短信封装
     *
     * 通过传入MsmVo对象，发送短信。
     *
     * @param msmVo 短信信息对象
     * @return 是否发送成功，发送成功返回true，否则返回false
     */
    @Override
    public boolean send(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            throw new YyghException(ResultCodeEnum.SERVICE_ERROR);
        }

        //整合阿里云短信服务
        //设置相关参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "我的尚医通学习网站");
        //模板code
        request.putQueryParameter("TemplateCode", msmVo.getTemplateCode());
        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 以上代码片段包含两个方法的实现：
     *
     * send(String phone, String code): 通过指定手机号和验证码，发送手机验证码。
     * send(MsmVo msmVo): MQ使用的发送短信封装方法。
     *
     * 这两个方法都涉及以下步骤：
     *
     * 第一步，判断手机号是否为空，如果为空则抛出YyghException异常。
     * 第二步，整合阿里云短信服务，设置相关参数。
     * 第三步，创建CommonRequest对象，并设置请求方法、域名、版本和操作。
     * 第四步，添加请求参数，包括手机号、签名名称、模板code以及验证码（使用JSON格式）。
     * 第五步，调用阿里云短信服务客户端的getCommonResponse()方法发送短信，并获取响应结果。
     * 第六步，根据响应结果判断短信是否发送成功，并返回相应的布尔值。
     *
     * */



}

