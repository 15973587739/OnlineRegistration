package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 操作科室信息的MongoDB库
 * 这个接口继承了 MongoRepository<Department, String>，表示它是一个 MongoDB 的仓库接口，用于对 Department 对象进行数据库操作。
 * @author SIYU
 */
@Repository //这个注解标识了该接口是一个仓库（Repository）接口，用于将数据访问的相关异常转换为 Spring 的统一异常体系。它是 Spring 提供的一种注解，用于标识数据访问层的 Bean。
public interface DepartmentRepository extends MongoRepository<Department, String> { //这个接口是 Spring Data MongoDB 提供的一个通用接口，用于简化 MongoDB 数据库的操作。它提供了一系列的方法，包括保存、查询、删除等常用的数据库操作。
    /**
     * 这是一个自定义的接口方法，用于根据医院代码（hoscode）和科室代码（depcode）获取对应的科室信息。Spring Data MongoDB 会根据方法名自动生成查询语句，无需手动编写查询逻辑。
     * 定义一个接口方法，用于根据医院代码和科室代码获取科室信息
     * @param hoscode 医院
     * @param depcode 科室
     * @return
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
