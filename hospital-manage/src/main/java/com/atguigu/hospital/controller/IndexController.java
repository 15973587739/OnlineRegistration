package com.atguigu.hospital.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;



/**
 * @author SIYU
 */
@Controller
public class IndexController {

	private final String LIST_INDEX = "redirect:/";

	private final static String PAGE_INDEX  = "frame/index";
	private final static String PAGE_MAIN  = "frame/main";
	private final static String PAGE_LOGIN  = "frame/login";
	private final static String PAGE_AUTH = "frame/auth";

	/**
	 * 框架首页
	 *
	 * @param model     数据模型
	 * @param request   HTTP请求对象
	 * @return          视图名称
	 */
	@RequestMapping(value = "/")
	public String index(ModelMap model, HttpServletRequest request) {
		return PAGE_INDEX;
	}

	/**
	 * 框架主页
	 *
	 * @return  视图名称
	 */
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main() {
		return PAGE_MAIN;
	}

	/**
	 * 框架授权页
	 *
	 * @return  视图名称
	 */
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public String auth() {
		return PAGE_AUTH;
	}

	/**
	 * 登录页
	 *
	 * @return  视图名称
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return PAGE_LOGIN;
	}
}

