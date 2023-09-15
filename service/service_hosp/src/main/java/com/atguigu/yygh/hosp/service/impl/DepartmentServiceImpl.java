package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author SIYU
 */
@Service // 标识层
@Slf4j // 日志注解
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 上传科室信息
     * @param paramMap 包含科室信息的参数映射
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        // paramMap 转换 department 对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);

        // 根据医院编号和科室编号查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        // 判断如果有就进行修改，没有就进行添加
        if (departmentExist != null) {
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    /**
     * 分页查询科室
     * @param page 当前页码
     * @param limit 每页记录数
     * @param departmentQueryVo 查询条件
     * @return 分页结果
     */
    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); // 改变默认大小写忽略方式：忽略大小写

        // 创建实例
        Example<Department> example = Example.of(department, matcher);
        return departmentRepository.findAll(example, pageable);
    }

    /**
     * 删除科室信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    /**
     * 根据医院编号，查询医院所有科室列表
     * @param hoscode 医院编号
     * @return 科室列表
     */

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();
        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        //Example SpringDateJPA中的一个类用来作于查询条件 这里表示把departmentQuery作为一个查询条件
        Example example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        //Map的key为所有大课室的编号list为小科室的集合
        Map<String, List<Department>> deparmentMap =
                //collect方法做分组   根据Bigcode进行分组 也就是通过大课室的编号进行分组
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap 将map集合中的数据转换成我们的实体类便于使用
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据 也就是所有的小科室信息
            List<Department> deparment1List = entry.getValue();
            //封装大科室 一个科室类有大科室的编号名称加多个小科室组成
            DepartmentVo departmentVo1 = new DepartmentVo();
            //封装大课室的编号
            departmentVo1.setDepcode(bigcode);
            //封装大课室的名称
            departmentVo1.setDepname(deparment1List.get(0).getBigname());

            // 封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department : departmentList) {
                DepartmentVo departmentVo2 = new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode()); // 小科室编号
                departmentVo2.setDepname(department.getDepname()); // 小科室名称
                children.add(departmentVo2);
            }

            // 把小科室列表放到大科室的 children 里面
            departmentVo1.setChildren(children);
            // 放到最终结果集 result 里面
            result.add(departmentVo1);
        }
        // 返回结果
        return result;
    }

    /**
     * 根据科室编号和医院编号查询科室名称
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 科室名称
     */
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return null;
    }

    /**
     * 根据医院编号和科室编号获取科室信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return 科室信息
     */
    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }

}
