package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.mapper.RefundInfoMapper;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author SIYU
 * 退款
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    /**
     * 保存退款信息
     *
     * @param paymentInfo 支付信息对象
     * @return 退款信息对象
     */
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        // 判断是否存在重复数据的添加
        QueryWrapper<RefundInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", paymentInfo.getOrderId());
        wrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(wrapper);

        // 如果退款表中已存在相同数据，则直接返回已存在的退款信息
        if (refundInfo != null) {
            return refundInfo;
        }

        // 添加退款记录
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());  // 创建时间
        refundInfo.setOrderId(paymentInfo.getOrderId());  // 订单ID
        refundInfo.setPaymentType(paymentInfo.getPaymentType());  // 支付类型
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());  // 订单编号
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());  // 退款状态
        refundInfo.setSubject(paymentInfo.getSubject());  // 订单信息
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());  // 订单金额
        baseMapper.insert(refundInfo);  // 将退款信息插入数据库

        // 返回退款信息对象
        return refundInfo;
    }

}
