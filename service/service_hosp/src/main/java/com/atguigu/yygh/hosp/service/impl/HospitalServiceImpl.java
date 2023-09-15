package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SIYU
 */
@Slf4j
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 保存医院信息
     * @param paramMap 包含医院信息的参数映射
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        // 将参数 paramMap 转换成 Hospital 对象
        log.info(JSONObject.toJSONString(paramMap));
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Hospital.class);
        // 判断是否存在
        Hospital targetHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        if (null != targetHospital) {
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            // 如果存在就进行修改
            hospitalRepository.save(hospital);
        } else {
            // 0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            // 不存在进行添加
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 分页查询医院列表
     * @param page 当前页码
     * @param limit 每页记录数
     * @param hospitalQueryVo 查询条件
     * @return 分页结果
     */
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); // 改变默认大小写忽略方式：忽略大小写

        // 创建实例
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        pages.getContent().stream().forEach(item -> {
            this.packHospital(item);
        });
        return pages;
    }

    /**
     * 根据医院编号返回医院信息
     * @param hoscode 医院编号
     * @return 医院信息
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }



    /**
     * 封装数据的方法
     * @param hospital 医院对象
     * @return 封装后的医院对象
     */
    private Hospital packHospital(Hospital hospital) {
        // 根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
        // 查询省市地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        // 封装医院等级（甲等、乙等....）
        hospital.getParam().put("hostypeString", hostypeString);
        // 封装地址
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }

    /**
     * 根据传入的status值修改状态
     * @param id 医院ID
     * @param status 状态值
     */
    @Override
    public void updateStatus(String id, Integer status) {
        // 判断status值是否合理
        if (status.intValue() == 0 || status.intValue() == 1) {
            // 根据id从MongoDB中获取Hospital对象
            Hospital hospital = hospitalRepository.findById(id).get();
            // 将新的status赋值
            hospital.setStatus(status);
            // 修改时间
            hospital.setUpdateTime(new Date());
            // 调用save方法进行修改
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 展示医院信息
     * @param id 医院ID
     * @return Map对象，包含医院信息和预约规则
     */
    @Override
    public Map<String, Object> show(String id) {
        // 将数据封装到map集合中做返回值返回出去
        Map<String, Object> result = new HashMap<>();
        // 通过get()方法获取一个Hospital对象然后调用packHospital方法获取到医院等级信息以及地区信息
        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);
        // 单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        // 不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    /**
     * 根据医院编号获取医院名称
     * @param hoscode 医院编号
     * @return 医院名称
     */
    @Override
    public String getHospName(String hoscode) {
        // 通过hosCode查询医院信息
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (null != hospital) {
            // 返回医院名字
            return hospital.getHosname();
        }
        return "";
    }

    /**
     * 根据医院名称模糊查询医院列表
     * @param hosname 医院名称
     * @return 匹配的医院列表
     */
    @Override
    public List<Hospital> findByHosname(String hosname) {
        // 从MongoDb中根据名字模糊查询
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    /**
     * 根据医院编号获取医院详情
     * @param hoscode 医院编号
     * @return Map对象，包含医院详情和预约规则
     */
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        // 医院详情，getByHoscode(hoscode)根据医院编号获取医院详情，packHospital封装医院地址和医院等级信息
        Hospital hospital = this.packHospital(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        // 预约规则
        result.put("bookingRule", hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }

}
