package com.atguigu.yygh.msm.service;

import com.atguigu.yygh.vo.msm.MsmVo;

/**
 * @author SIYU
 */
public interface MsmService {

    /**
     * 发送手机验证码
     *
     * 通过指定手机号和验证码，发送手机验证码。
     *
     * @param phone 手机号码
     * @param code  验证码
     * @return 是否发送成功，发送成功返回true，否则返回false
     */
    boolean send(String phone, String code);

    /**
     * MQ使用的发送短信接口
     *
     * 通过传入MsmVo对象，发送短信消息。
     *
     * @param msmVo 短信信息对象
     * @return 是否发送成功，发送成功返回true，否则返回false
     */
    boolean send(MsmVo msmVo);

}
