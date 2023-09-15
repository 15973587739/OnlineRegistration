package com.atguigu.yygh.order.controller.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.WeixinService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 微信支付接口控制器
 */
@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {
    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 下单，生成二维码
     *
     * @param orderId 订单ID
     * @return 生成的二维码结果
     */
    @GetMapping("/createNative/{orderId}")
    public Result createNative(
            @ApiParam(name = "orderId", value = "订单ID", required = true)
            @PathVariable("orderId") Long orderId) {
        return Result.ok(weixinService.createNative(orderId));
    }

    /**
     * 查询订单支付状态
     *
     * @param orderId 订单ID
     * @return 支付状态结果
     */
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPatStuatus(@PathVariable Long orderId){
        // 调用微信接口实现支付查询状态信息
        Map<String,String> resultMap = weixinService.queryPayStatus(orderId);
        // 判断支付状态
        if(resultMap == null){// 微信接口中没有查询到相应的订单
            return Result.fail().message("支付出错");
        }
        if("SUCCESS".equals(resultMap.get("trade_state"))){// 支付成功
            // 更新订单状态
            String out_trade_no = resultMap.get("out_trade_no"); // 获取订单编号
            paymentService.paySuccess(out_trade_no,resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }
}
