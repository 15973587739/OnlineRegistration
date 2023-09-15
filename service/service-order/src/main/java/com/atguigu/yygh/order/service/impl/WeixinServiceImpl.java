package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.atguigu.yygh.order.service.WeixinService;
import com.atguigu.yygh.order.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.order.utils.HttpClient;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RefundInfoService refundInfoService;

    /**
     * 根据订单号下单，生成支付链接
     *
     * @param orderId 订单号
     * @return 返回生成的支付链接
     */
    @Override
    public Map createNative(Long orderId) {
        try {
            //从redis获取二维码数据 如果没有就通过调用微信接口生成二维码 订单号为key
            Map payMap = (Map) redisTemplate.opsForValue().get(orderId.toString());
            if(null != payMap) return payMap;
            //根据订单id获取订单信息
            OrderInfo order = orderService.getById(orderId);
            // 保存交易记录
            paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
            //1、设置参数 调用微信生成二维码接口
            //把参数转换成xml格式，使用商户key进行加密 （微信端只认识xml格式参数WXPayUtil提供了加密方法）
            Map paramMap = new HashMap();
            //关联公众号的id
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            //商户号
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            //生成随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //主题内容
            String body = order.getReserveDate() + "就诊"+ order.getDepname();
            paramMap.put("body", body);
            //订单号
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //订单金额
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//订单金额，测试用，金额0.01
            //访问ip
            paramMap.put("spbill_create_ip", "127.0.0.1");
            //回调路径
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            //支付方式
            paramMap.put("trade_type", "NATIVE");
            //2、HTTPClient来根据URL访问第三方接口并且传递参数 调用微信生成二维码接口
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            // WXPayUtil工具类将参数转换成xml格式并且进行加密 微信接口只认xml格式     generateSignedXml（参数数据，加密秘钥）
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            //发送post请求
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            //转换成map集合
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //4、封装返回结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));
            if(null != resultMap.get("result_code")) {
                //微信支付二维码2小时过期，可采取2小时未支付取消订单
                // 向Redis中添加二维码数据2小时内可直接从Redis中获取 以订单号做key 二维码数据做value
                redisTemplate.opsForValue().set(orderId.toString(), map, 1000, TimeUnit.MINUTES);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    /**
     * 调用微信接口实现支付查询状态
     *
     * @param orderId 订单号
     * @return 返回支付查询状态结果
     */
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            //1.根据orderId获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);

            //2.封装提交参数
            Map paramMap = new HashMap();
            ////公众号ID
            paramMap.put("appid",ConstantPropertiesUtils.APPID);
            //商户号
            paramMap.put("mch_id",ConstantPropertiesUtils.PARTNER);
            //订单交易号
            paramMap.put("out_trade_no",orderInfo.getOutTradeNo());
            //随机生成的字符串
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());

            //3.设置请求内容
            //设置httpClient的请求路径 路径为微信提供的查询支付状态的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            //WXPayUtil工具类将参数转换成xml格式并且进行加密 微信接口只认xml格式
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //4.得到微信接口返回的数据 返回的xml格式
            String xml = client.getContent();
            //利用WXPayUtil工具类进行转换
            Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("支付状态resultMap"+resultMap);
            //5.把接口数据返回
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 微信退款接口
     * 微信退款操作
     *
     * @param orderId 订单号
     * @return 退款结果，成功返回true，失败返回false
     */
    @Override
    public Boolean refund(Long orderId) {
        try {
            //获取支付记录信息
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
            //添加信息到退款记录表中
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            //如果已经退款则直接结束方法 返回true
            if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()){
                return true;
            }
            //调用微信接口实现退款
            //封装接口所需要的参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",ConstantPropertiesUtils.APPID);  //公众号ID
            paramMap.put("mch_id",ConstantPropertiesUtils.PARTNER); //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr()); //随机字符串
            paramMap.put("transaction_id",paymentInfo.getTradeNo());//微信订单编号
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo());//商户订单编号
            paramMap.put("out_refund_no",paymentInfo.getOutTradeNo());//商户退款单号
            paramMap.put("total_fee","1");
            paramMap.put("refund_fee","1");
            String paramXml = WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY);
            //设置调用接口内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(paramXml);
            client.setHttps(true);
            //设置退款证书
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            client.post();
            //接收数据
            String xml = client.getContent();
            Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);
            //如果退款成功了
            if(null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))){
                //修改状态为已退款
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据订单号去微信第三方查询支付状态
     *
     * @param orderId     订单号
     * @param paymentType 支付类型
     * @return 返回支付查询状态结果
     */
    @Override
    public Map queryPayStatus(Long orderId, String paymentType) {
        return null;
    }


}
