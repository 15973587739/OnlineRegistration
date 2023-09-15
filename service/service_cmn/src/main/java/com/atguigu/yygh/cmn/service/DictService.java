package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author SIYU
 * 这段代码是一个接口 DictService，定义了一些数据字典相关的方法。我为每个方法添加了文档注释，以提供方法的说明和参数的描述。
 */
public interface DictService extends IService<Dict> {

    /**
     * 根据数据id查询子数据列表
     * @param id 数据id
     * @return 子数据列表
     */
    List<Dict> findChildDate(Long id);

    /**
     * 导出数据字典
     * @param response HTTP响应对象
     */
    void exportDictData(HttpServletResponse response);

    /**
     * 导入数据字典
     * @param file 导入的文件
     */
    void importDictData(MultipartFile file);

    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode 上级编码
     * @param value 值
     * @return 数据字典名称
     */
    String getNameByParentDictCodeAndValue(String parentDictCode, String value);


    /**
     * 根据字典编码查询数据字典列表
     * @param dictCode 字典编码
     * @return 数据字典列表
     */
    List<Dict> findByDictCode(String dictCode);

    /**
     * 根据字典编码和值查询数据字典名称
     * @param dictCode 字典编码
     * @param value 值
     * @return 数据字典名称
     */
    String getDictName(String dictCode, String value);

    /**
     * 根据值查询数据字典名称
     * @param value 值
     * @return 数据字典名称
     */
    String getDictName(String value);
}
