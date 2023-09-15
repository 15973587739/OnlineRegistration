package com.atguigu.yygh.hosp.mapper;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医院设置数据访问接口
 * 该接口用于操作医院设置的数据，包括增删改查等操作。
 * @author SIYU
 */
@Mapper
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {

}
