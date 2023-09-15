package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 病人服务接口
 *
 * 该接口定义了对病人信息进行操作的方法。
 * @author SIYU
 */
public interface PatientService extends IService<Patient> {
    /**
     * 获取指定用户的就诊人列表
     *
     * @param userId 用户ID
     * @return 就诊人列表
     */
    List<Patient> findAllUserId(Long userId);

    /**
     * 根据ID获取就诊人信息
     *
     * @param id 就诊人ID
     * @return 就诊人信息
     */
    Patient getPatientId(Long id);
}
