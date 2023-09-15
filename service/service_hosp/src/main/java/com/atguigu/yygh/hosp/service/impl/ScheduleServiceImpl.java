package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.mapper.ScheduleMapper;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;



    /**
     * 将传入的参数保存为排班信息
     * @param paramMap 参数Map，包含排班信息
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        // 将paramMap转换成String类型
        String paramMapString = JSONObject.toJSONString(paramMap);
        // 将String类型的paramMap转换成Schedule对象
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        // 根据医院编号和排班编号查询已存在的排班信息
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        // 判断是否已存在排班信息
        if (scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    /**
     * 分页查询排班信息
     * @param page 当前页码
     * @param limit 每页记录数
     * @param scheduleQueryVo 查询条件对象
     * @return 分页结果集
     */
    @Override
    public Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 模糊查询
                .withIgnoreCase(true); // 忽略大小写

        // 创建实例
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> pages = scheduleRepository.findAll(example, pageable);
        return pages;
    }

    /**
     * 根据医院编号和排班编号删除排班信息
     * @param hoscode 医院编号
     * @param hosScheduleId 排班编号
     */
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        // 根据医院编号和排班编号查询排班信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        // 判断是否查询到排班信息
        if (null != schedule) {
            scheduleRepository.deleteById(schedule.getId()); // 删除排班信息
        }
    }

    /**
     * 根据医院编号和科室编号查询排班规则数据
     * @param page 当前页码
     * @param limit 每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 包含排班规则数据的Map集合
     */
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        // 1. 根据医院编号和科室编号查询每个工作日的科室可预约总数和剩余预约数（查询条件）
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // 2. 根据工作日进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria), // 分组匹配条件
                Aggregation.group("workDate") // 需要分组的字段
                        .first("workDate").as("workDate") // 分组后的日期别名
                        // 3. 统计号源数量
                        .count().as("docCount") // 就诊医生人数
                        .sum("reservedNumber").as("reservedNumber") // 科室可预约数总和
                        .sum("availableNumber").as("availableNumber"), // 科室剩余可预约数总和
                Aggregation.sort(Sort.Direction.DESC, "workDate"), // 排序
                Aggregation.skip((page - 1) * limit), // 分页
                Aggregation.limit(limit)
        );
        // 调用方法，执行获取结果
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        // 分组查询的总记录数，用于分页
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria), // 查询条件
                Aggregation.group("workDate") // 根据workDate分组
        );
        // 执行查询总记录数
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        // 将日期转换为星期几
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate)); // 获取星期几
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // 设置最终数据
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList); // 排班规则数据列表
        result.put("total", total); // 总记录数

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        //添加到最终数据中
        result.put("baseMap",baseMap);

        return result;
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime 时间
     * @return 星期
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

    /**
     * 根据医院编号、科室编号和工作日期查询排班详细信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @param workDate 工作日期
     * @return 排班详细信息列表
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        // 根据参数查询 MongoDB
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        // 遍历列表，设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    /**
     * 封装排班详情的其他值：医院名称、科室名称、日期对应星期
     * @param schedule 排班详情
     */
    private void packageSchedule(Schedule schedule) {
        // 设置医院名称
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        // 设置科室名称
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        // 设置日期对应星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 获取预约排班规则和信息
     * @param page 当前页码
     * @param limit 每页大小
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 预约规则和信息的Map结果
     */
    @Override
    public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();

        // 获取预约规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (null == hospital) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();

        // 获取可预约日期分页数据
        IPage iPage = this.getListDate(page, limit, bookingRule);

        // 当前页可预约日期
        List<Date> dateList = iPage.getRecords();

        // 获取可预约日期科室剩余预约数
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);

        // 聚合操作，用于统计可预约日期的相关信息
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );

        // 进行聚合查询，获取预约规则信息
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregationResults.getMappedResults();

        // 合并统计数据到预约规则Vo
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        // 获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0, len = dateList.size(); i < len; i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            if (null == bookingScheduleRuleVo) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            // 计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            // 设置最后一页最后一条记录的状态为即将预约
            if (i == len - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // 如果当前页为第一页，且是当天的预约，如果已过停号时间，则设置状态为停止预约
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        // 可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());

        // 其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    /**
     * 获取可预约日期分页数据
     * @param page 当前页码
     * @param limit 每页大小
     * @param bookingRule 预约规则
     * @return 可预约日期的分页结果
     */
    private IPage<Date> getListDate(int page, int limit, BookingRule bookingRule) {
        // 获取当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());

        // 获取预约周期
        int cycle = bookingRule.getCycle();

        // 如果当天放号时间已过，则预约周期后一天为即将放号时间，周期加1
        // 放号结束时间为十一点半，所以如果当前时间大于等于放号结束时间，则只能预约后一天的号
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }

        // 获取所有可预约的日期，最后一天用于显示即将放号倒计时
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        // 进行日期分页，每页最多显示7天的数据
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;

        // 如果可显示数据小于7天，则直接显示
        if (end > dateList.size()) {
            end = dateList.size();
        }

        // 进行分页
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }

        // 最终查询结果
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将日期（yyyy-MM-dd HH:mm）转换为DateTime对象
     * @param date 日期
     * @param timeString 时间字符串
     * @return DateTime对象
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    /**
     * 根据排班ID获取排班信息
     * @param id 排班ID
     * @return 排班信息
     */
    @Override
    public Schedule getById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        return this.packSchedule(schedule);
    }

    /**
     * 根据排班ID获取预约下单数据
     * @param scheduleId 排班ID
     * @return 预约下单数据
     */
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        // 获取排班信息
        Schedule schedule = scheduleRepository.getScheduleById(scheduleId);
        if (schedule == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 根据医院编号获取医院信息
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 获取医院信息中的预约规则
        BookingRule bookingRule = hospital.getBookingRule();
        if (null == bookingRule) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //编号
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        //名称
        scheduleOrderVo.setHosname(hospitalService.getHospName(schedule.getHoscode()));
        //科室编号
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        //科室名称
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //排班编号
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        //剩余预约数
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        //医生职责
        scheduleOrderVo.setTitle(schedule.getTitle());
        //安排日期
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        //安排时间
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        //服务费
        scheduleOrderVo.setAmount(schedule.getAmount());

        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());
        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());
        return scheduleOrderVo;
    }

    /**
     * 更新MongoDB中的排班信息，用于消息队列的操作
     * @param schedule 要更新的排班信息
     */
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    /**
     * 封装排班详情的其他值，包括医院名称、科室名称和日期对应的星期
     * @param schedule 排班信息
     * @return 封装后的排班信息
     */
    private Schedule packSchedule(Schedule schedule) {
        // 设置医院名称
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        // 设置科室名称
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        // 设置日期对应的星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }
}
