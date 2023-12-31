package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医院管理接口
 * @author SIYU
 */
@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 获取医院分页列表
     * @param page 当前页码
     * @param limit 每页记录数
     * @param hospitalQueryVo 查询对象
     * @return 分页列表结果
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("list/{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,

            @ApiParam(name = "hospitalQueryVo", value = "查询对象", required  = false)
            HospitalQueryVo hospitalQueryVo) {
        return Result.ok(hospitalService.selectPage(page, limit, hospitalQueryVo));
    }

    /**
     * 更新医院上线状态
     * @param id 医院id
     * @param status 状态（0：未上线 1：已上线）
     * @return 更新结果
     */
    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result lock(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable("id") String id,
            @ApiParam(name = "status", value = "状态（0：未上线 1：已上线）", required = true)
            @PathVariable("status") Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 获取医院详情
     * @param id 医院id
     * @return 医院详情
     */
    @ApiOperation(value = "获取医院详情")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable String id) {
        return Result.ok(hospitalService.show(id));
    }

}
