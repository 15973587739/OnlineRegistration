package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 退款信息服务接口
 * @author SIYU
 */
public interface RefundInfoService extends IService<RefundInfo> {

    /**
     * 保存退款记录
     *
     * @param paymentInfo 支付信息对象
     * @return 保存后的退款信息对象
     */
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo);

}
