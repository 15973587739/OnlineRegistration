package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author SIYU
 * 处理与用户相关的请求
 */
@RestController //是一个Spring注解，用于标识该类是一个RESTful风格的控制器。它表示该类中的方法将处理HTTP请求并返回响应。
@RequestMapping("/admin/user") // 是一个Spring注解，用于指定控制器处理的请求路径的前缀。在这里，它指示该控制器处理路径以"/admin/user"开头的请求。
public class UserController {

    @Autowired //是一个Spring注解，用于自动注入userInfoService属性。通过依赖注入，可以在控制器中使用userInfoService来执行与用户信息相关的操作。
    private UserInfoService userInfoService;

    /**
     * 用户列表（带条件分页查询）
     *
     * @param page            当前页码
     * @param limit           每页记录数
     * @param userInfoQueryVo 用户信息查询条件
     * @return 分页查询结果
     */
    @ApiOperation(value = "用户列表")
    @GetMapping("list/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        // 分页条件 Page对象
        Page<UserInfo> pageParam = new Page<>(page, limit);
        // 传入分页条件Page对象和条件查询条件userInfoQueryVo
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam, userInfoQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 锁定用户
     *
     * @param userId 用户ID
     * @param status 锁定状态（0-未锁定，1-已锁定）
     * @return 操作结果
     */
    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public Result lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status) {
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    /**
     * 用户详情查询
     *
     * @param userId 用户ID
     * @return 用户详情和其关联的就诊人信息
     */
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId) {
        // 查询用户信息和该用户下的就诊人信息
        Map<String, Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    /**
     * 认证审批
     *
     * @param userId     用户ID
     * @param authStatus 认证状态（0-未认证，1-已认证）
     * @return 操作结果
     */
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId, @PathVariable Integer authStatus) {
        userInfoService.approval(userId, authStatus);
        return Result.ok();
    }
}
