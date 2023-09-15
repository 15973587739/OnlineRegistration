package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author SIYU
 * 这段代码是一个实现类 `DictServiceImpl`，实现了 `DictService` 接口中定义的方法。我为每个方法添加了注释，说明了方法的功能和参数的含义。
 */
@Service
@CacheConfig(cacheNames = "DictService")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 根据数据id查询子数据列表
     * @param id 数据id
     * @return 子数据列表
     */
    @Cacheable(value = "dict", keyGenerator = "keyGenerator") //缓存注解
    @Override
    public List<Dict> findChildDate(Long id) {
        // 查询条件
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);

        // 向每个dict对象设置hasChildren属性
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }

        return dictList;
    }

    /**
     * 导出数据字典
     * @param response HTTP响应对象
     */
    @Override
    public void exportDictData(HttpServletResponse response) {
        // 设置类型
        response.setContentType("application/vnd.ms-excel");
        // 设置编码
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 查询数据库
        List<Dict> dictList = baseMapper.selectList(null);

        // 转换Dict对象为DictEeVo对象
        List<DictEeVo> dictEeVoList = new ArrayList<>();
        for (Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            dictEeVoList.add(dictEeVo);
        }

        try {
            // 调用EasyExcel方法进行写操作
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据字典
     * @param file 导入的文件
     */
    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public void importDictData(MultipartFile file) {
        try {
            // 调用EasyExcel方法进行读操作
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断指定id下是否有子数据
     * @param id 数据id
     * @return 子数据存在返回true，否则返回false
     */
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        // 如果有数据就返回true
        return count > 0;
    }

    /**
     * 根据字典编码获取字典对象
     * @param dictCode 字典编码
     * @return 字典对象
     */
    private Dict getByDictCode(String dictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        return dict;
    }


    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode 上级编码
     * @param value 值
     * @return 数据字典名称
     */
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        // 如果value能唯一定位数据字典，parentDictCode可以传空，例如：省市区的value值能够唯一确定
        if (StringUtils.isEmpty(parentDictCode)) {
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if (null != dict) {
                return dict.getName();
            }
        } else {
            Dict parentDict = this.getByDictCode(parentDictCode);
            if (null == parentDict) {
                return "";
            }
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentDict.getId()).eq("value", value));
            if (null != dict) {
                return dict.getName();
            }
        }
        return "";
    }

    /**
     * 根据字典编码查询数据字典列表
     * @param dictCode 字典编码
     * @return 数据字典列表
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict codeDict = this.getByDictCode(dictCode);
        if (null == codeDict) {
            return null;
        }
        return this.findChildDate(codeDict.getId());
    }

    /**
     * 根据字典编码和值查询数据字典名称
     * @param dictCode 字典编码
     * @param value 值
     * @return 数据字典名称
     */
    @Override
    public String getDictName(String dictCode, String value) {
        // 如果dictCode为空，直接根据value查询
        if (StringUtils.isEmpty(dictCode)) {
            // 直接根据value查询
            QueryWrapper<Dict> queryWrapper = new QueryWrapper();
            queryWrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        } else {
            // 根据dictCode和value查询
            Dict dict = this.getByDictCode(dictCode);
            Long id = dict.getId();
            // 根据parent_id和value查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", id).eq("value", value));
            return finalDict.getName();
        }
    }

    /**
     * 根据值查询数据字典名称
     * @param value 值
     * @return 数据字典名称
     */
    @Override
    public String getDictName(String value) {
        return this.getDictName("", value);
    }
}

