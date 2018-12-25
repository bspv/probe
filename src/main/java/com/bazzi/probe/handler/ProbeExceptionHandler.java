package com.bazzi.probe.handler;

import com.bazzi.core.ex.BusinessException;
import com.bazzi.core.result.Result;
import com.bazzi.core.util.RenderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class ProbeExceptionHandler {

	@ResponseStatus(value = HttpStatus.OK)
	@ExceptionHandler(BusinessException.class)
	public ModelAndView handleException(BusinessException ex, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("CODE：{},MESSAGE：{}", ex.getCode(), ex.getMessage());
		ModelAndView view = new ModelAndView();
		if (isAjax(request)) {
			// AJAX
			Result<?> result = new Result<>();
			result.setError(ex.getCode(), ex.getMessage());
			RenderUtil.renderJson(response, result);
			return view;
		} else {
			// 普通请求
			view.addObject("code", ex.getCode());
			view.addObject("message", ex.getMessage());
			view.setViewName("error/error");
			return view;
		}
	}

	@ResponseStatus(value = HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		log.error(ex.getMessage(), ex);
		if (response != null && !response.isCommitted()) {
			try {
				request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex);
				request.getRequestDispatcher("/error").forward(request, response);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return;
	}

	private static boolean isAjax(HttpServletRequest request) {
		return request.getHeader("accept").contains("application/json")
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").contains("XMLHttpRequest"));
	}

}
