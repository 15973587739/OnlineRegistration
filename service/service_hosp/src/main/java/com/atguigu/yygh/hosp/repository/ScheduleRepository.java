package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author SIYU
 * 操作排班的MongoDb库
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    /**
     * 根据医生ID查询排班信息
     *
     * @param id 医生ID
     * @return 排班对象
     */
    Schedule getScheduleById(String id);
    //Schedule：返回类型为 Schedule，表示该方法会返回一个 Schedule 对象，即排班信息
    //getScheduleById：方法名表示根据医生ID进行查询。id 表示医生ID

    /**
     * 根据医院编号和排班ID查询排班信息
     *
     * @param hoscode       医院编号
     * @param hosScheduleId 排班ID
     * @return 排班对象
     */
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
    //Schedule：返回类型为 Schedule，表示该方法会返回一个 Schedule 对象，即排班信息。
    //getScheduleByHoscodeAndHosScheduleId：方法名表示根据医院编号和排班ID进行查询。hoscode 表示医院编号，hosScheduleId 表示排班ID。

    /**
     * 根据医院编号、科室编号和工作日期查询排班详细信息
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param toDate   工作日期
     * @return 排班列表
     */
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
    //List<Schedule>：返回类型为 List<Schedule>，表示该方法会返回一个排班列表，其中每个元素都是 Schedule 对象。
    //findScheduleByHoscodeAndDepcodeAndWorkDate：方法名表示根据医院编号、科室编号和工作日期进行查询。hoscode 表示医院编号，depcode 表示科室编号，toDate 表示工作日期。
}
