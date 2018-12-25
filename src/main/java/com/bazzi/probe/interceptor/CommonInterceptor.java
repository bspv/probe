package com.bazzi.probe.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CommonInterceptor extends HandlerInterceptorAdapter {

	private NamedThreadLocal<Long> timeThreadLocal = new NamedThreadLocal<>("StopWatch-StartTime");

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		timeThreadLocal.set(System.currentTimeMillis());
		return true;
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		log.debug("{} used time:{}", request.getRequestURI(), System.currentTimeMillis() - timeThreadLocal.get());
	}
}
