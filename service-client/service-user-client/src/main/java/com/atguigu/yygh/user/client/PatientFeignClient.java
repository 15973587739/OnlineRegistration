package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * PatientFeignClient是用于与service-user服务进行通信的Feign客户端接口。
 */
@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {

    /**
     * 根据就诊人ID获取就诊人信息。
     *
     * @param id 就诊人ID。
     * @return 就诊人对象。
     */
    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatientOrder(
            @PathVariable("id") Long id);

    /**
     * 以上是给定代码段的文档注释。
     * 注释包括对接口和方法的详细说明。
     * 在该注释中，我们描述了PatientFeignClient接口的用途，并针对getPatientOrder方法提供了说明。
     * 注释中还包含了参数id的说明，以及该方法的返回值类型。
     * 这样的注释可以帮助其他开发人员理解代码的功能和用法，并提供必要的文档信息
     */

}

