package com.atguigu.hospital.config;

import com.atguigu.hospital.util.YyghException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理类
 * @author SIYU
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法，用于处理Exception及其子类的异常。
     * @param e 异常对象
     * @return 错误页面的视图名称
     */
    @ExceptionHandler(Exception.class) //注解来指定要处理的异常类型。
    public String error(Exception e){
        e.printStackTrace();
        return "error";
    }


    /**
     * 自定义异常处理方法
     * @param e 异常对象
     * @return 错误
     */
    @ExceptionHandler(YyghException.class)
    public String error(YyghException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
