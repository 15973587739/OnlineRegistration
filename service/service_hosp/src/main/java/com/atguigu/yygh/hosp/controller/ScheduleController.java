package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
/**
 * 排班
 * @author SIYU
 */
@Slf4j
@Api(tags = "医院排班数据(MongoDB数据库)")
@RestController
@RequestMapping("/admin/hosp/schedule") // 请求路径注解
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据医院编号和科室编号，查询排班规则数据
     * @param page 分页参数，当前页数
     * @param limit 分页参数，每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 查询结果
     */
    @ApiOperation(value ="获取可预约排班数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    /**
     * 根据医院编号、科室编号和工作日期，查询排班详细信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @param workDate 工作日期
     * @return 查询结果
     */
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(list);
    }
}
