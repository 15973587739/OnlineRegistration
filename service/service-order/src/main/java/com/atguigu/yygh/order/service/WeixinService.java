package com.atguigu.yygh.order.service;

import java.util.Map;

/**
 * @author SIYU
 * 微信接口
 */
public interface WeixinService {
    /**
     * 根据订单号下单，生成支付链接
     *
     * @param orderId 订单号
     * @return 返回生成的支付链接
     */
    Map createNative(Long orderId);

    /**
     * 调用微信接口实现支付查询状态
     *
     * @param orderId 订单号
     * @return 返回支付查询状态结果
     */
    Map<String, String> queryPayStatus(Long orderId);

    /**
     * 微信退款接口
     *
     * @param orderId 订单号
     * @return 退款结果，成功返回true，失败返回false
     */
    Boolean refund(Long orderId);

    /**
     * 根据订单号去微信第三方查询支付状态
     *
     * @param orderId     订单号
     * @param paymentType 支付类型
     * @return 返回支付查询状态结果
     */
    Map queryPayStatus(Long orderId, String paymentType);
}

