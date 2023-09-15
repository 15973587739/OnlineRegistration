package com.atguigu.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

/**
 * 字典监听器，用于解析并处理字典数据的Excel导入
 * @author SIYU
 */
public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    /**
     * 构造函数
     * @param dictMapper 字典Mapper对象
     */
    public DictListener(DictMapper dictMapper){
        this.dictMapper=dictMapper;
    }

    /**
     * 逐行读取Excel数据，并将数据添加到数据库中
     * @param dictEeVo 字典数据对象
     * @param analysisContext 分析上下文对象
     */
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        // 创建字典对象
        Dict dict = new Dict();
        // 将字典数据对象转换为字典对象
        BeanUtils.copyProperties(dictEeVo,dict);
        // 调用Mapper将字典对象插入数据库
        dictMapper.insert(dict);
    }

    /**
     * 在所有数据解析完成后执行的操作
     * @param analysisContext 分析上下文对象
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
