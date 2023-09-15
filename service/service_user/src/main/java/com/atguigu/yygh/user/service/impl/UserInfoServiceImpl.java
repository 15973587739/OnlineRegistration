package com.atguigu.yygh.user.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SIYU
 */
@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private PatientService patientService;

    /**
     * 用户手机号登录接口
     *
     * @param loginVo 登录信息
     * @return 包含登录结果的Map对象
     */
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //校验参数
        if(StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //绑定手机号码
        UserInfo userInfo = null;
        //如果openid有值不为空就去数据库查   微信绑定登录  前端根据openid判断是否是微信登录
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            //如果有就把手机号绑定
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        //如果userinfo为空，进行正常手机登录
        if(userInfo == null) {
            //判断是否第一次登录：根据手机号查询数据库，如果不存在相同手机号就是第一次登录
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone",phone);
            userInfo = baseMapper.selectOne(wrapper);
            if(userInfo == null) { //第一次使用这个手机号登录
                //添加信息到数据库
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //不是第一次，直接登录
        //返回登录信息
        //返回登录用户名
        //返回token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name",name);

        //jwt生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);
        return map;

        /**
         * login登录
         * Override：表示该方法是对父类或接口中同名方法的重写。
         * public Map<String, Object> login(LoginVo loginVo)：方法的签名，指定了返回类型和参数。
         * String phone = loginVo.getPhone() 和 String code = loginVo.getCode()：获取登录请求中的手机号和验证码。
         * 校验参数：检查手机号和验证码是否为空，如果为空则抛出参数错误的自定义异常。
         * // TODO 校验校验验证码：标记着需要校验验证码的代码，但是实际上还没有实现。
         * 从 Redis 中获取存储的手机验证码，与用户输入的验证码进行比较，如果不匹配则抛出验证码错误的自定义异常。
         * 绑定手机号码：根据 openid 判断是否为微信登录，如果是则将手机号码与用户信息进行绑定。
         * 如果用户信息为空，则执行正常的手机登录流程。
         * 判断是否第一次登录：根据手机号查询数据库，如果不存在相同手机号则表示是第一次登录。
         * 添加用户信息到数据库：创建新的 UserInfo 对象，将手机号码和其他信息存入数据库。
         * 校验是否被禁用：检查用户的状态是否为禁用状态，如果是则抛出登录被禁用的自定义异常。
         * 返回登录信息：构造一个包含登录用户名和 token 的 Map 对象，并将其返回。
         */
    }




    /**
     * 根据openid查询用户信息
     *
     * @param openid 用户的openid
     * @return 用户信息对象
     */
    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        return userInfo;
    }

    /**
     * 用户认证
     *
     * @param userId      用户ID
     * @param userAuthVo  用户认证信息
     */
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息 （查询是那个用户在进行认证 id通过前端的请求传递过来）
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息 把认证信息添加到用户类中
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);

    }

    /**
     * 用户列表（条件查询带分页）
     *
     * @param pageParam        分页参数
     * @param userInfoQueryVo  查询条件
     * @return 分页查询结果
     */
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!org.springframework.util.StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!org.springframework.util.StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!org.springframework.util.StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!org.springframework.util.StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!org.springframework.util.StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }

    /**
     * 用户锁定
     *
     * @param userId  用户ID
     * @param status  锁定状态（0：未锁定，1：已锁定）
     */
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue()==0 || status.intValue()==1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 用户详情
     *
     * @param userId  用户ID
     * @return 包含用户详情的Map对象
     */
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patientList",patientList);
        return map;
    }

    /**
     * 认证审批
     *
     * @param userId       用户ID
     * @param authStatus   认证状态
     */
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 封装用户信息
     *
     * @param userInfo 用户信息对象
     * @return 封装后的用户信息对象
     */
    private UserInfo packageUserInfo(UserInfo userInfo) {
        // 处理认证状态编码
        // 根据状态码通过 AuthStatusEnum 枚举类转换成具体的状态
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));

        // 处理用户状态 0  1
        // 如果状态为 0，设置状态字符串为 "锁定"，否则设置为 "正常"
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString", statusString);

        // 返回封装后的用户信息对象
        return userInfo;
    }


}
