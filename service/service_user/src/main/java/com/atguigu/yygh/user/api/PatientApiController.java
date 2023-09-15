package com.atguigu.yygh.user.api;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 患者API控制器
 * 就诊人管理接口
 * 该控制器提供了对患者信息进行操作的API接口。
 */
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {
    @Autowired
    private PatientService patientService;

    /**
     * 获取当前登录用户的就诊人列表
     *
     * @param request HTTP请求对象
     * @return 就诊人列表
     */
    @GetMapping("auth/findAll")
    public Result finAll(HttpServletRequest request){
        // 获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        // 根据用户id查询就诊人信息
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.ok(list);
    }

    /**
     * 添加就诊人
     *
     * @param patient  就诊人对象
     * @param request  HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody  Patient patient, HttpServletRequest request){
        // 获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        // 添加就诊人
        patientService.save(patient);
        return Result.ok();
    }

    /**
     * 根据ID获取就诊人信息
     *
     * @param id 就诊人ID
     * @return 就诊人信息
     */
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable Long id){
        // 直接调用查询方法数据不够完善，所以这里重新写了一个方法进行了远程服务调用完善数据
        Patient patient = patientService.getPatientId(id);
        return Result.ok(patient);
    }

    /**
     * 修改就诊人信息
     *
     * @param patient  就诊人对象
     * @return 操作结果
     */
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody  Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    /**
     * 删除就诊人
     *
     * @param id 就诊人ID
     * @return 操作结果
     */
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }

    /**
     * 获取就诊人信息
     *
     * @param id 就诊人ID
     * @return 就诊人信息
     */
    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(
            @ApiParam(name = "id", value = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        // 根据id查询就诊人信息
        return patientService.getById(id);
    }
}
