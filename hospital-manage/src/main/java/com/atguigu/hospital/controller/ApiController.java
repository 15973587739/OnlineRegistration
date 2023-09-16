package com.atguigu.hospital.controller;

import com.atguigu.hospital.mapper.HospitalSetMapper;
import com.atguigu.hospital.model.HospitalSet;
import com.atguigu.hospital.service.ApiService;
import com.atguigu.hospital.util.YyghException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * 医院管理接口的API控制器类。
 */
@Api(tags = "医院管理接口")
@Controller
@RequestMapping
public class ApiController extends BaseController {

	@Autowired
	private ApiService apiService;

	@Autowired
	private HospitalSetMapper hospitalSetMapper;

	/**
	 * 获取医院设置信息的方法。
	 *
	 * @param model              数据模型
	 * @param redirectAttributes 重定向属性
	 * @return 医院设置页面的视图名称
	 */
	@RequestMapping("/hospitalSet/index")
	public String getHospitalSet(ModelMap model, RedirectAttributes redirectAttributes) {
		HospitalSet hospitalSet = hospitalSetMapper.selectById(1);
		model.addAttribute("hospitalSet", hospitalSet);
		return "hospitalSet/index";
	}

	/**
	 * 创建医院设置信息的方法。
	 *
	 * @param model       数据模型
	 * @param hospitalSet 医院设置对象
	 * @return 重定向至医院设置页面
	 */
	@RequestMapping(value = "/hospitalSet/save")
	public String createHospitalSet(ModelMap model, HospitalSet hospitalSet) {
		hospitalSetMapper.updateById(hospitalSet);
		return "redirect:/hospitalSet/index";
	}

	/**
	 * 获取医院信息的方法。
	 *
	 * @param model              数据模型
	 * @param request            HTTP请求对象
	 * @param redirectAttributes 重定向属性
	 * @return 医院页面的视图名称
	 */
	@RequestMapping("/hospital/index")
	public String getHospital(ModelMap model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		try {
			HospitalSet hospitalSet = hospitalSetMapper.selectById(1);
			if (null == hospitalSet || StringUtils.isEmpty(hospitalSet.getHoscode()) ||
					StringUtils.isEmpty(hospitalSet.getSignKey())) {
				this.failureMessage("先设置医院code与签名key", redirectAttributes);
				return "redirect:/hospitalSet/index";
			}

			model.addAttribute("hospital", apiService.getHospital());
		} catch (YyghException e) {
			this.failureMessage(e.getMessage(), request);
		} catch (Exception e) {
			this.failureMessage("数据异常", request);
		}
		return "hospital/index";
	}

	/**
	 * 创建医院的方法。
	 *
	 * @param model 数据模型
	 * @return 医院创建页面的视图名称
	 */
	@RequestMapping(value = "/hospital/create")
	public String createHospital(ModelMap model) {
		return "hospital/create";
	}

	/**
	 * 保存医院信息的方法。
	 *
	 * @param data    医院数据
	 * @param request HTTP请求对象
	 * @return 保存成功页面或保存失败页面的视图名称
	 */
	@RequestMapping(value = "/hospital/save", method = RequestMethod.POST)
	public String saveHospital(String data, HttpServletRequest request) {
		try {
			apiService.saveHospital(data);
		} catch (YyghException e) {
			return this.failurePage(e.getMessage(), request);
		} catch (Exception e) {
			return this.failurePage("数据异常", request);
		}
		return this.successPage(null, request);
	}

	/**
	 * 获取科室列表的方法。
	 *
	 * @param model              数据模型
	 * @param pageNum            分页页码
	 * @param pageSize           分页大小
	 * @param request            HTTP请求对象
	 * @param redirectAttributes 重定向属性
	 * @return 科室列表页面的视图名称
	 */
	@RequestMapping("/department/list")
	public String findDepartment(ModelMap model,
								 @RequestParam(defaultValue = "1") int pageNum,
								 @RequestParam(defaultValue = "10") int pageSize,
								 HttpServletRequest request, RedirectAttributes redirectAttributes) {
		try {
			HospitalSet hospitalSet = hospitalSetMapper.selectById(1);
			if (null == hospitalSet || StringUtils.isEmpty(hospitalSet.getHoscode()) ||
					StringUtils.isEmpty(hospitalSet.getSignKey())) {
				this.failureMessage("先设置医院code与签名key", redirectAttributes);
				return "redirect:/hospitalSet/index";
			}

			model.addAllAttributes(apiService.findDepartment(pageNum, pageSize));
		} catch (YyghException e) {
			this.failureMessage(e.getMessage(), request);
		} catch (Exception e) {
			this.failureMessage("数据异常", request);
		}
		return "department/index";
	}

	/**
	 * 创建科室的方法。
	 *
	 * @param model 数据模型
	 * @return 科室创建页面的视图名称
	 */
	@RequestMapping(value = "/department/create")
	public String create(ModelMap model) {
		return "department/create";
	}

	/**
	 * 保存科室信息的方法。
	 *
	 * @param data    科室数据
	 * @param request HTTP请求对象
	 * @return 保存成功页面或保存失败页面的视图名称
	 */
	@RequestMapping(value = "/department/save", method = RequestMethod.POST)
	public String save(String data, HttpServletRequest request) {
		try {
			apiService.saveDepartment(data);
		} catch (YyghException e) {
			return this.failurePage(e.getMessage(), request);
		} catch (Exception e) {
			return this.failurePage("数据异常", request);
		}
		return this.successPage(null, request);
	}

	/**
	 * 获取排班列表的方法。
	 *
	 * @param model              数据模型
	 * @param pageNum            分页页码
	 * @param pageSize           分页大小
	 * @param request            HTTP请求对象
	 * @param redirectAttributes 重定向属性
	 * @return 排班列表页面的视图名称
	 */
	@RequestMapping("/schedule/list")
	public String findSchedule(ModelMap model,
							   @RequestParam(defaultValue = "1") int pageNum,
							   @RequestParam(defaultValue = "10") int pageSize,
							   HttpServletRequest request, RedirectAttributes redirectAttributes) {
		try {
			HospitalSet hospitalSet = hospitalSetMapper.selectById(1);
			if (null == hospitalSet || StringUtils.isEmpty(hospitalSet.getHoscode()) || StringUtils.isEmpty(hospitalSet.getSignKey())) {
				this.failureMessage("先设置医院code与签名key", redirectAttributes);
				return "redirect:/hospitalSet/index";
			}

			model.addAllAttributes(apiService.findSchedule(pageNum, pageSize));
		} catch (YyghException e) {
			this.failureMessage(e.getMessage(), request);
		} catch (Exception e) {
			this.failureMessage("数据异常", request);
		}
		return "schedule/index";
	}

	/**
	 * 创建排班页面的方法。
	 *
	 * @param model 数据模型
	 * @return 排班创建页面的视图名称
	 */
	@RequestMapping(value = "/schedule/create")
	public String createSchedule(ModelMap model) {
		return "schedule/create";
	}

	/**
	 * 保存排班信息的方法。
	 *
	 * @param data    排班数据
	 * @param request HTTP请求对象
	 * @return 保存成功页面或保存失败页面的视图名称
	 */
	@RequestMapping(value = "/schedule/save", method = RequestMethod.POST)
	public String saveSchedule(String data, HttpServletRequest request) {
		try {
			// 数据预处理
			// data = data.replaceAll("
			// ", "").replace(" ", "");
			apiService.saveSchedule(data);
		} catch (YyghException e) {
			return this.failurePage(e.getMessage(), request);
		} catch (Exception e) {
			e.printStackTrace();
			return this.failurePage("数据异常：" + e.getMessage(), request);
		}
		return this.successPage(null, request);
	}

	/**
	 * 批量创建医院页面的方法。
	 *
	 * @param model 数据模型
	 * @return 批量创建医院页面的视图名称
	 */
	@RequestMapping(value = "/hospital/createBatch")
	public String createHospitalBatch(ModelMap model) {
		return "hospital/createBatch";
	}

	/**
	 * 批量保存医院信息的方法。
	 *
	 * @param request HTTP请求对象
	 * @return 保存成功页面或保存失败页面的视图名称
	 */
	@RequestMapping(value = "/hospital/saveBatch", method = RequestMethod.POST)
	public String saveBatchHospital(HttpServletRequest request) {
		try {
			apiService.saveBatchHospital();
		} catch (YyghException e) {
			return this.failurePage(e.getMessage(), request);
		} catch (Exception e) {
			return this.failurePage("数据异常", request);
		}
		return this.successPage(null, request);
	}

	/**
	 * 删除科室的方法。
	 *
	 * @param model              数据模型
	 * @param depcode            科室代码
	 * @param redirectAttributes 重定向属性
	 * @return 重定向至科室列表页面
	 */
	@RequestMapping(value = "/department/remove/{depcode}", method = RequestMethod.GET)
	public String removeDepartment(ModelMap model, @PathVariable String depcode, RedirectAttributes redirectAttributes) {
		apiService.removeDepartment(depcode);

		this.successMessage(null, redirectAttributes);
		return "redirect:/department/list";
	}

	/**
	 * 删除排班的方法。
	 *
	 * @param model              数据模型
	 * @param hosScheduleId      排班ID
	 * @param redirectAttributes 重定向属性
	 * @return 重定向至排班列表页面
	 */
	@RequestMapping(value = "/schedule/remove/{hosScheduleId}", method = RequestMethod.GET)
	public String removeSchedule(ModelMap model, @PathVariable String hosScheduleId, RedirectAttributes redirectAttributes) {
		apiService.removeSchedule(hosScheduleId);

		this.successMessage(null, redirectAttributes);
		return "redirect:/schedule/list";
	}
}

