package com.atguigu.hospital.controller;


import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 基础控制器类，提供一些通用方法和提示信息。
 */
public class BaseController {

    // 提示信息
    public final static String MESSAGE_SUCCESS = "操作成功！";
    public final static String MESSAGE_FAILURE = "操作失败！";

    /**
     * 成功提示
     *
     * @param message               提示消息
     * @param redirectAttributes    重定向属性
     */
    protected void successMessage(String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", StringUtils.isEmpty(message) ? MESSAGE_SUCCESS : message);
        redirectAttributes.addFlashAttribute("messageType", 1);
    }

    /**
     * 失败提示
     *
     * @param message               提示消息
     * @param redirectAttributes    重定向属性
     */
    protected void failureMessage(String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", StringUtils.isEmpty(message) ? MESSAGE_FAILURE : message);
        redirectAttributes.addFlashAttribute("messageType", 0);
    }

    /**
     * 失败提示
     *
     * @param message   提示消息
     * @param request   HTTP请求对象
     */
    protected void failureMessage(String message, HttpServletRequest request) {
        request.setAttribute("message", StringUtils.isEmpty(message) ? MESSAGE_SUCCESS : message);
        request.setAttribute("messageType", 0);
    }

    /**
     * 成功页
     *
     * @param message   提示消息
     * @param request   HTTP请求对象
     * @return          成功页视图名称
     */
    protected String successPage(String message, HttpServletRequest request) {
        request.setAttribute("messagePage", StringUtils.isEmpty(message) ? MESSAGE_SUCCESS : message);
        return "common/successPage";
    }

    /**
     * 失败页
     *
     * @param message   提示消息
     * @param request   HTTP请求对象
     * @return          失败页视图名称
     */
    protected String failurePage(String message, HttpServletRequest request) {
        request.setAttribute("messagePage", StringUtils.isEmpty(message) ? MESSAGE_FAILURE : message);
        return "common/failurePage";
    }

    /**
     * 获取过滤器参数
     *
     * @param request   HTTP请求对象
     * @return          过滤器参数映射
     */
    protected Map<String, Object> getFilters(HttpServletRequest request) {
        Map<String, Object> filters = WebUtils.getParametersStartingWith(request, "s_");
        return filters;
    }

}
