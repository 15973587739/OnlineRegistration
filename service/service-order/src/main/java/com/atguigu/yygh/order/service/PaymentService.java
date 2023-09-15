package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 支付服务接口
 * @author SIYU
 */
public interface PaymentService extends IService<PaymentInfo> {
    /**
     * 保存交易记录
     *
     * @param order       订单信息对象
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);

    /**
     * 更新订单状态
     *
     * @param out_trade_no  支付订单号
     * @param resultMap    支付结果信息
     */
    void paySuccess(String out_trade_no, Map<String,String> resultMap);

    /**
     * 获取支付记录
     *
     * @param orderId      订单ID
     * @param paymentType  支付类型
     * @return 支付信息对象
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);
}
