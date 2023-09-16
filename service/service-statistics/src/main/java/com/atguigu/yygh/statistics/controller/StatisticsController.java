package com.atguigu.yygh.statistics.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.order.client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计管理接口
 * @author SIYU
 */
@Api(tags = "统计管理接口")
@RestController
@RequestMapping("admin/statistics")
public class StatisticsController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 获取统计数据
     *
     * @param orderCountQueryVo 查询对象
     * @return 统计数据结果
     */
    @ApiOperation(value = "获取统计数据")
    @GetMapping("getCountMap")
    public Result getCountMap(@ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false)
                              OrderCountQueryVo orderCountQueryVo) {
        // 调用Feign客户端获取统计数据
        Map<String, Object> countMap = orderFeignClient.getCountMap(orderCountQueryVo);
        return Result.ok(countMap);
    }
}
