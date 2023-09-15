package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author SIYU
 * 操作医院信息的MongoDB库
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    /**
     * 通过医院代码查询医院信息
     *
     * @param hoscode 医院代码
     * @return 医院对象
     */
    Hospital getHospitalByHoscode(String hoscode);
    //Hospital：返回类型为 Hospital，表示该方法会返回一个 Hospital 对象，即医院信息。
    //getHospitalByHoscode：方法名表示通过医院代码进行查询。hoscode 表示医院代码。


    /**
     * 根据医院名称模糊查询医院列表
     *
     * @param hosname 医院名称
     * @return 医院列表
     */
    List<Hospital> findHospitalByHosnameLike(String hosname);
    //List<Hospital>：返回类型为 List<Hospital>，表示该方法会返回一个医院列表，其中每个元素都是 Hospital 对象。
    //findHospitalByHosnameLike：方法名表示根据医院名称进行模糊查询。hosname 表示医院名称。



}
