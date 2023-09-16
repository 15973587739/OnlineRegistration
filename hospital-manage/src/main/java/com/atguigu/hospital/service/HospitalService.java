package com.atguigu.hospital.service;

import java.io.IOException;
import java.util.Map;

/**
 * 医院服务接口，定义了与医院相关的操作方法。
 * @author SIYU
 */
public interface HospitalService {

    /**
     * 预约下单
     *
     * @param paramMap 包含预约订单信息的参数映射
     * @return  包含操作结果的Map对象
     */
    Map<String, Object> submitOrder(Map<String, Object> paramMap);

    /**
     * 更新支付状态
     *
     * @param paramMap 包含支付状态更新信息的参数映射
     */
    void updatePayStatus(Map<String, Object> paramMap);

    /**
     * 更新取消预约状态
     *
     * @param paramMap 包含取消预约状态更新信息的参数映射
     */
    void updateCancelStatus(Map<String, Object> paramMap);
}
