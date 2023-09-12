package com.atguigu.yygh.common.exception;

import com.atguigu.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author SIYU
 */
@ControllerAdvice //表明该类是一个全局异常处理器
public class GlobalExceptionHandler {

    /**
     * 全局异常处理，捕获Exception类的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class) //表明该方法用于处理 Exception 类型的异常
    @ResponseBody //表明方法返回的是响应体数据，而不是视图。
    public Result error(Exception e) {
        e.printStackTrace(); //打印异常跟踪信息，用于调试和定位问题
        return Result.fail(); //返回失败结果
    }

    //全局异常处理，捕获YyException类的异常
    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result error(YyghException e) {
        e.printStackTrace();
        return Result.fail(); //fail()方法用于构建失败结果对象
    }
}
