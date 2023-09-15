package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 用户信息服务接口
 * @author SIYU
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户手机号登录接口
     *
     * @param loginVo 登录信息
     * @return 包含登录结果的Map对象
     */
    Map<String, Object> login(LoginVo loginVo);

    /**
     * 根据openid查询用户信息
     *
     * @param openid 用户的openid
     * @return 用户信息对象
     */
    UserInfo selectWxInfoOpenId(String openid);

    /**
     * 用户认证
     *
     * @param userId      用户ID
     * @param userAuthVo  用户认证信息
     */
    void userAuth(Long userId, UserAuthVo userAuthVo);

    /**
     * 用户列表（条件查询带分页）
     *
     * @param pageParam        分页参数
     * @param userInfoQueryVo  查询条件
     * @return 分页查询结果
     */
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    /**
     * 用户锁定
     *
     * @param userId  用户ID
     * @param status  锁定状态（0：未锁定，1：已锁定）
     */
    void lock(Long userId, Integer status);

    /**
     * 用户详情
     *
     * @param userId  用户ID
     * @return 包含用户详情的Map对象
     */
    Map<String, Object> show(Long userId);

    /**
     * 认证审批
     *
     * @param userId       用户ID
     * @param authStatus   认证状态
     */
    void approval(Long userId, Integer authStatus);
}
