package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 订单服务接口
 * @author SIYU
 */
public interface OrderService extends IService<OrderInfo> {
    /**
     * 保存订单
     *
     * @param scheduleId 预约排班ID
     * @param patientId  患者ID
     * @return 保存后的订单ID
     */
    Long saveOrder(String scheduleId, Long patientId);

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单信息对象
     */
    OrderInfo getOrder(String orderId);

    /**
     * 分页查询当前用户的订单信息
     *
     * @param pageParam     分页参数
     * @param orderQueryVo  订单查询条件对象
     * @return 订单信息分页列表
     */
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 取消预约
     *
     * @param orderId 订单ID
     * @return 取消成功返回true，否则返回false
     */
    Boolean cancelOrder(Long orderId);

    /**
     * 获取订单统计数据
     *
     * @param orderCountQueryVo 订单统计查询对象
     * @return 订单统计数据的Map对象
     */
    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

    /**
     * 患者提醒
     */
    void patientTips();
}
