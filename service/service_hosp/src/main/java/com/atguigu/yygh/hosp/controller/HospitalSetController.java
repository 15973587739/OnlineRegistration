package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * 医院设置管理
 */
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {


    @Autowired // 注入service
    private HospitalSetService hospitalSetService;


    /**
     * 查询医院设置表所有信息
     * @return 所有医院设置信息列表
     */
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet() {
        // 调用service的方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }


    /**
     * 逻辑删除医院设置
     * @param id 医院设置id
     * @return 删除结果
     */
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id) {
        // 物理删除
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setIsDelete(1);
        boolean flag = hospitalSetService.updateById(hospitalSet);
        // 逻辑删除医院设置
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 条件查询带分页
     * @param current 当前页
     * @param limit 每页记录数
     * @param hospitalSetQueryVo 查询对象
     * @return 分页查询结果
     */
    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);
        // 构建条件
        String hosname = "";
        String hoscode = "";
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if (hospitalSetQueryVo != null) {
            hosname = hospitalSetQueryVo.getHosname(); // 医院名称
            hoscode = hospitalSetQueryVo.getHoscode(); // 医院编号
        }
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }
        wrapper.eq("is_deleted", 0);
        // 调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);
        // 返回结果
        return Result.ok(pageHospitalSet);
    }

    /**
     * 添加医院设置
     * @param hospitalSet 医院设置对象
     * @return 添加结果
     */
    @ApiOperation(value = "添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置状态：1 使用，0 不能使用
        hospitalSet.setStatus(1);
        // 签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        // 调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据id获取医院设置
     * @param id 医院设置id
     * @return 医院设置对象
     */
    @ApiOperation(value = "根据id获取医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    /**
     * 修改医院设置
     * @param hospitalSet 医院设置对象
     * @return 修改结果
     */
    @ApiOperation(value = "修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 批量删除医院设置
     * @param idList 医院设置id列表
     * @return 删除结果
     */
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        // 物理删除
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setIsDelete(1);
        for (int i = 0; i < idList.size(); i++) {
            hospitalSet.setId(idList.get(i));
            hospitalSetService.updateById(hospitalSet);
        }
        return Result.ok();
    }

    /**
     * 医院设置锁定和解锁
     * @param id 医院设置id
     * @param status 状态（锁定/解锁）
     * @return 锁定或解锁结果
     */
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status) {
        // 根据id查询医院当前状态
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置状态
        hospitalSet.setStatus(status);
        // 调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

}
