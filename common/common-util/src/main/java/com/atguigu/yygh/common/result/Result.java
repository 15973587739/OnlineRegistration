package com.atguigu.yygh.common.result;

import com.atguigu.yygh.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 这是一个全局统一返回结果类 Result，用于封装接口的返回结果。
 * @author SIYU
 */
@Data
@ApiModel(value = "全局统一返回结果")
public class Result<T> {

    /**
     * 返回码，表示接口返回结果的状态。
     */
    @ApiModelProperty(value = "返回码")
    private Integer code;

    /**
     * 返回消息，用于描述接口返回结果的详细信息。
     */
    @ApiModelProperty(value = "返回消息")
    private String message;

    /**
     * 返回的数据对象。
     */
    @ApiModelProperty(value = "返回数据")
    private T data;

    /**
     * 默认构造方法
     */
    public Result(){}

    /**
     * 根据数据构建Result对象，数据可以为null
     * @param data 返回的数据
     * @param <T>  返回数据的类型
     * @return 构建的Result对象
     * @code  根据指定的数据构建返回结果对象，数据可以为null。
     */
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    /**
     * 根据数据和结果枚举类型构建Result对象
     * @param body          返回的数据
     * @param resultCodeEnum 结果枚举类型
     * @param <T>           返回数据的类型
     * @return 构建的Result对象
     */
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    /**
     * 根据返回码和返回消息构建Result对象，数据为null
     * @param code    返回码
     * @param message 返回消息
     * @param <T>     返回数据的类型
     * @return 构建的Result对象
     */
    public static <T> Result<T> build(Integer code, String message) {
        Result<T> result = build(null);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 操作成功，返回成功的Result对象，数据为null
     * @param <T> 返回数据的类型
     * @return 操作成功的Result对象
     */
    public static<T> Result<T> ok(){
        return Result.ok(null);
    }

    /**
     * 操作成功，返回带有数据的Result对象
     * @param data 返回的数据
     * @param <T>  返回数据的类型
     * @return 带有数据的操作成功的Result对象
     */
    public static<T> Result<T> ok(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    /**
     * 操作失败，返回失败的Result对象，数据为null
     * @param <T> 返回数据的类型
     * @return 操作失败的Result对象
     */
    public static<T> Result<T> fail(){
        return Result.fail(null);
    }

    /**
     * 操作失败，返回带有数据的Result对象
     * @param data 返回的数据
     * @param <T>  返回数据的类型
     * @return 带有数据的操作失败的Result对象
     */
    public static<T> Result<T> fail(T data){
        Result<T> result = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    /**
     * 设置消息，并返回当前Result对象
     * @param msg 返回消息
     * @return 当前Result对象
     */
    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    /**
     * 设置返回码，并返回当前Result对象
     * @param code 返回码
     * @return 当前Result对象
     */
    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    /**
     * 判断返回结果是否成功
     * @return 返回结果是否成功的布尔值
     */
    public boolean isOk() {
        if(this.getCode().intValue() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return true;
        }
        return false;
    }
}
