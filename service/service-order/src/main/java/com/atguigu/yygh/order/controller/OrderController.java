package com.atguigu.yygh.order.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.service.OrderService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * @author SIYU
 * 订单
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PatientFeignClient patientFeignClient;

    /**
     * 获取订单列表的分页结果
     *
     * @param page         当前页码
     * @param limit        每页记录数
     * @param orderQueryVo 查询对象
     * @param request      HTTP请求对象
     * @return 分页结果
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false) OrderQueryVo orderQueryVo,
            HttpServletRequest request) {
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 获取订单状态列表
     *
     * @return 订单状态列表
     */
    @ApiOperation(value = "获取订单状态")
    @GetMapping("getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId 订单ID
     * @return 包含订单信息和患者信息的Map对象
     */
    @GetMapping("show/{orderId}")
    public Result getOrders(@PathVariable String orderId) {
        Map map = new HashMap();
        OrderInfo orderInfo = orderService.getOrder(orderId);
        Patient patientOrder = patientFeignClient.getPatientOrder(orderInfo.getPatientId());
        map.put("orderInfo", orderInfo);
        map.put("patient", patientOrder);
        return Result.ok(map);
    }

//    /**
//     * 获取订单统计数据
//     *
//     * @param orderCountQueryVo 订单统计查询对象
//     * @return 订单统计数据的Map对象
//     */
//    @ApiOperation(value = "获取订单统计数据")
//    @PostMapping("inner/getCountMap")
//    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
//        return orderService.getCountMap(orderCountQueryVo);
//    }

    /**
     * 取消预约
     *
     * @param orderId 订单ID
     * @return 取消预约结果
     */
    @ApiOperation(value = "取消预约")
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        return Result.ok(orderService.cancelOrder(orderId));
    }
}

