package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author SIYU
 * 数据字典接口控制器类
 */
@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 导入数据字典接口
     * @param file 导入的文件
     * @return 导入结果
     */
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }

    /**
     * 导出数据字典接口
     * @param response HTTP响应对象
     */
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
    }

    /**
     * 根据数据id查询子数据列表
     * @param id 数据id
     * @return 子数据列表
     */
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildDate(id);
        return Result.ok(list);
    }

    /**
     * 根据dictCode获取下级节点
     * @param dictCode 节点编码
     * @return 下级节点列表
     */
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(
            @ApiParam(name = "dictCode", value = "节点编码", required = true)
            @PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    /**
     * 根据dictCode和value查询字典名称
     * @param dictCode 字典编码
     * @param value 字典值
     * @return 字典名称
     */
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode ,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }

    /**
     * 根据value查询字典名称
     * @param value 字典值
     * @return 字典名称
     */
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName(value);
        return dictName;
    }

}
