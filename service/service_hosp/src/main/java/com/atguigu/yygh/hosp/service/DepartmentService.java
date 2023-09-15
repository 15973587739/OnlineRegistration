package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author SIYU
 * 这个接口定义了部门服务的方法
 */
public interface DepartmentService {
    /**
     * 上传科室信息
     * @param paramMap 包含科室信息的参数映射
     */
    void save(Map<String, Object> paramMap);

    /**
     * 分页查询科室
     * @param page 当前页码
     * @param limit 每页记录数
     * @param departmentQueryVo 查询条件
     * @return 分页结果
     */
    Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    /**
     * 删除科室
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    void remove(String hoscode, String depcode);

    /**
     * 根据医院编号，查询医院所有科室列表
     * @param hoscode 医院编号
     * @return 医院的科室列表
     */
    List<DepartmentVo> findDeptTree(String hoscode);

    /**
     * 根据科室编号和医院编号，查询科室名称
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 科室名称
     */
    String getDepName(String hoscode, String depcode);


    /**
     * 根据医院编号和科室编号获取部门信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 部门信息
     */
    Department getDepartment(String hoscode, String depcode);



}
