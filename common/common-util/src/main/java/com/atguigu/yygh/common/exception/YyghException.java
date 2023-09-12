package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 自定义全局异常类
 * @author SIYU
 */
@Data //该注解是 lombok 提供的，用于自动生成 getter、setter、equals、hashcode 等常用方法
@ApiModel(value = "自定义全局异常类") //该注解用于 Swagger 文档生成，指定该类在文档中的名称。
public class YyghException extends RuntimeException {

    /**
     * 异常状态码，用于标识异常的具体类型。
     */
    @ApiModelProperty(value = "异常状态码") //该注解用于 Swagger 文档生成，指定该属性在文档中的说明。
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message 异常的错误消息
     * @param code 异常的状态码
     */
    public YyghException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum ResultCodeEnum枚举类型对象
     */
    public YyghException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    /**
     * 返回异常的字符串表示形式
     * @return 异常的字符串表示形式
     */
    @Override
    public String toString() {
        return "YyghException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';  //返回异常的字符串表示形式，包括异常的状态码和错误消息。
    }
}
