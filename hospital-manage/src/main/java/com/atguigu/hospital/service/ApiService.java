package com.atguigu.hospital.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * 接口类，定义了与互联网服务API相关的方法。
 * @author SIYU
 */
public interface ApiService {

    /**
     * 获取医院编码
     *
     * @return 医院编码
     */
    String getHoscode();

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    String getSignKey();

    /**
     * 获取医院信息
     *
     * @return 医院信息的JSON对象
     */
    JSONObject getHospital();

    /**
     * 保存医院信息
     *
     * @param data 待保存的医院信息
     * @return 保存是否成功的布尔值
     */
    boolean saveHospital(String data);

    /**
     * 分页查询科室信息
     *
     * @param pageNum   当前页码
     * @param pageSize  每页记录数
     * @return  包含科室信息的Map对象
     */
    Map<String, Object> findDepartment(int pageNum, int pageSize);

    /**
     * 保存科室信息
     *
     * @param data 待保存的科室信息
     * @return 保存是否成功的布尔值
     */
    boolean saveDepartment(String data);

    /**
     * 删除科室信息
     *
     * @param depcode   待删除的科室编码
     * @return 删除是否成功的布尔值
     */
    boolean removeDepartment(String depcode);

    /**
     * 分页查询排班信息
     *
     * @param pageNum   当前页码
     * @param pageSize  每页记录数
     * @return  包含排班信息的Map对象
     */
    Map<String, Object> findSchedule(int pageNum, int pageSize);

    /**
     * 保存排班信息
     *
     * @param data 待保存的排班信息
     * @return 保存是否成功的布尔值
     */
    boolean saveSchedule(String data);

    /**
     * 删除排班信息
     *
     * @param hosScheduleId 待删除的排班ID
     * @return 删除是否成功的布尔值
     */
    boolean removeSchedule(String hosScheduleId);

    /**
     * 批量保存医院信息
     *
     * @throws IOException 如果发生IO异常
     */
    void saveBatchHospital() throws IOException;
}
