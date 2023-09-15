package com.atguigu.yygh.user.mapper;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author SIYU
 */
public interface PatientMapper extends BaseMapper<Patient> {
    /**
     * PatientMapper 是一个接口，它继承自 BaseMapper<Patient>。
     *
     * BaseMapper<Patient> 是一个泛型接口，它定义了一些基本的CRUD（创建、读取、更新、删除）操作方法，以及其他常见的数据库操作。Patient 是指定的实体类，表示数据库中的一个表或实体。
     *
     * 通过继承 BaseMapper<Patient>，PatientMapper 接口继承了所有在 BaseMapper 接口中定义的方法。这意味着你可以在 PatientMapper 中直接使用这些方法来操作与 Patient 实体相关的数据库表。例如，你可以使用 insert、selectById、selectList 等方法来插入、按ID查询、查询列表等操作。
     *
     * 该接口的目的是为了提供对 Patient 数据库表的访问和操作。通过该接口，你可以方便地使用已定义好的方法执行数据库操作，而不需要显式编写这些操作的实现代码。请确保 Patient 类的定义和 BaseMapper 接口的导入是正确的。
     */
}
