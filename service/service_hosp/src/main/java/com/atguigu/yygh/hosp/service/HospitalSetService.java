package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author SIYU
 * 医院设置服务接口
 *
 * 该接口定义了对医院设置的相关操作，包括增删改查等功能。
 * 继承自 IService 接口，提供了常见的服务操作方法。
 */
public interface HospitalSetService extends IService<HospitalSet> {
    /**
     * 获取签名key
     * @param hoscode 医院编号
     * @return
     */
    String getSignKey(String hoscode);

    /**
     * 根据医院编号获取医院签名信息
     * @param hoscode 医院编号
     * @return 医院签名信息
     */
    SignInfoVo getSignInfoVo(String hoscode);

}
