package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * @author SIYU
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    //注入远程调用接口
    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 获取指定用户的就诊人列表
     *
     * @param userId 用户ID
     * @return 就诊人列表
     */
    @Override
    public List<Patient> findAllUserId(Long userId) {
        //根据userid查询所有就诊人信息列表
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        //通过远程调用，得到编码对应具体内容，查询数据字典表内容
        patientList.stream().forEach(item -> {
            //其他参数封装  封装前进行判断该用户是否填写了该类数据
                this.packPatient(item);
        });
        return patientList;
    }

    /**
     * 根据ID获取就诊人信息
     *
     * @param id 就诊人ID
     * @return 就诊人信息
     */
    @Override
    public Patient getPatientId(Long id) {
        //进行远程服务调用 完善就诊人信息
        Patient patient = baseMapper.selectById(id);
        return packPatient(patient);
    }


    /**
     * Patient对象里面其他参数封装
     * @param patient 就诊人
     * @return 就诊人信息
     */
    private Patient packPatient(Patient patient) {
        //根据证件类型编码，获取证件类型具体指
        String certificatesTypeString = "" ;
        if(!StringUtils.isEmpty(patient.getCertificatesType())){
            certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());//联系人证件
        }
        //联系人证件类型
        String contactsCertificatesTypeString = "";
        if(!StringUtils.isEmpty(patient.getContactsCertificatesType())){
            contactsCertificatesTypeString =
                    dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        }
        //省
        String provinceString = "";
        if(!StringUtils.isEmpty(patient.getProvinceCode())){
            provinceString = dictFeignClient.getName(patient.getProvinceCode());
        }
        //市
        String cityString = "" ;
        if(!StringUtils.isEmpty(patient.getCityCode())){
            cityString = dictFeignClient.getName(patient.getCityCode());
        }
        //区
        String districtString = "" ;
        if(!StringUtils.isEmpty(patient.getDistrictCode())){
            districtString = dictFeignClient.getName(patient.getDistrictCode());
        }
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }

}
