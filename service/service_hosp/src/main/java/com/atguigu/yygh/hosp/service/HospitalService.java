package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author SIYU
 * 医院服务接口
 */
public interface HospitalService {

    /**
     * 上传医院信息
     *
     * @param paramMap 医院信息参数
     */
    void save(Map<String, Object> paramMap);

    /**
     * 分页查询
     * @param page 当前页码
     * @param limit 每页记录数
     * @param hospitalQueryVo 查询条件
     * @return
     */
    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    /**
     * 查询医院
     * @param hoscode 医院编号
     * @return
     */
    Hospital getByHoscode(String hoscode);

    /**
     * 更新上线状态
     */
    void updateStatus(String id, Integer status);

    /**
     * 医院详情
     * @param id 编号
     * @return
     */
    Map<String, Object> show(String id);

    /**
     * 根据医院编号获取医院名称接口
     * @param hoscode 医院编号
     * @return
     */
    String getHospName(String hoscode);

    /**
     * 根据医院名称查找医院列表
     * @param hosname 医院名称
     * @return 匹配医院名称的医院列表
     */
    List<Hospital> findByHosname(String hosname);



    /**
     * 根据医院编号获取医院预约挂号详情
     * @param hoscode 医院编号
     * @return
     */
    Map<String, Object> item(String hoscode);
}

