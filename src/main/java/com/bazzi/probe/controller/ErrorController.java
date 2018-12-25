package com.bazzi.probe.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.bazzi.core.annotation.AllowAccess;
import com.bazzi.core.result.Result;

@Controller
public class ErrorController {

	/**
	 * 异常页面
	 * 
	 * @param request
	 * @param ex
	 * @param response
	 * @return
	 */
	@AllowAccess
	@RequestMapping(path = "/error", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, Exception ex, HttpServletResponse response) {
		int status = response.getStatus();
		ModelAndView modelAndView;
		if (status == HttpStatus.OK.value()) {// 异常
			Object obj = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
			if (obj == null) {
				obj = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
			}
			if (obj != null && Exception.class.isAssignableFrom(obj.getClass())) {
				ex = (Exception) obj;
			}
			modelAndView = new ModelAndView("error/error");
			modelAndView.addObject("message", ex.getMessage());
		} else {// 403、404、500等错误
			response.setStatus(HttpStatus.OK.value());
			modelAndView = new ModelAndView("error/error");
			modelAndView.addObject("message", status + HttpStatus.valueOf(status).getReasonPhrase());
		}
		return modelAndView;
	}

	/**
	 * 异常JSON
	 * 
	 * @param request
	 * @param ex
	 * @param response
	 * @return
	 */
	@AllowAccess
	@ResponseBody
	@RequestMapping(path = "/error")
	public Result<?> errorJson(HttpServletRequest request, Exception ex, HttpServletResponse response) {
		Result<?> result = new Result<>();
		int status = response.getStatus();
		if (status == HttpStatus.OK.value()) {// 异常
			Object obj = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
			if (obj == null) {
				obj = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
			}
			if (obj != null && Exception.class.isAssignableFrom(obj.getClass())) {
				ex = (Exception) obj;
			}
			result.setError("-1", ex.getMessage());
		} else {// 403、404、500等错误
			result.setError(String.valueOf(status), HttpStatus.valueOf(status).getReasonPhrase());
		}
		return result;
	}

}
