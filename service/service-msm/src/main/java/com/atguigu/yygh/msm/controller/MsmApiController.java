package com.atguigu.yygh.msm.controller;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.util.ConstantPropertiesUtils;
import com.atguigu.yygh.msm.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短信发送
 * @author SIYU
 */
@Api(tags = "短信发送api")
@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 发送短信验证码
     *
     * @param phone 手机号码
     * @return 发送结果
     */
    @ApiOperation(value = "短信发送")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        // 从redis获取验证码，如果获取到，返回ok
        // key为手机号，value为验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }

        // 如果从redis获取不到验证码
        // 生成验证码
        code = RandomUtil.getSixBitRandom();
        // 调用service方法，通过整合短信服务进行发送
        boolean isSend = msmService.send(phone, code);
        // 将生成的验证码放入redis中，设置有效时间
        if(isSend) {
            // 设置两分钟失效
            redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.MINUTES);
            return Result.ok();
        } else {
            return Result.fail().message("发送短信失败");
        }
    }
}
