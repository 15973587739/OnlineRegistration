package com.atguigu.yygh.order.client;

import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.aspectj.weaver.ast.Or;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * OrderFeignClient是用于与service-order服务进行通信的Feign客户端接口。
 * @author SIYU
 */
@FeignClient(value = "service-order")
@Repository
public interface OrderFeignClient {

    /**
     * 获取预约数据的统计信息。
     *
     * @param orderCountQueryVo 包含查询条件的OrderCountQueryVo对象。
     * @return 包含统计结果的键值对。
     */
    @PostMapping("/admin/order/orderInfo/inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
    /**
     * 以上是一个使用Feign进行服务间通信的接口定义。该接口使用@FeignClient注解指定了要调用的服务名为service-order。接口还添加了@Repository注解，表示该接口用于数据访问。
     *
     * 接口中定义了一个名为getCountMap的方法，使用了@PostMapping注解来指定该方法是一个POST请求。
     * 该方法调用了/api/order/orderInfo/inner/getCountMap接口，传递了一个OrderCountQueryVo对象作为请求体，用于查询预约数据的统计信息。
     * 方法的返回值是一个Map<String, Object>，包含了统计结果的键值对。
     */

}
