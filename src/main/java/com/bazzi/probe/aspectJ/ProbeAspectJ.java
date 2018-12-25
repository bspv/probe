package com.bazzi.probe.aspectJ;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bazzi.core.ex.BusinessException;
import com.bazzi.core.util.IpUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Aspect
@Component
public class ProbeAspectJ {

	@Pointcut("execution(public * com.bazzi.probe.controller.*.*(..))")
	public void aspectController() {
	}

	@Pointcut("execution(public * com.bazzi.probe.service.impl.*.*(..))")
	public void aspectService() {
	}

	@Around("aspectController()")
	public Object handlerParam(ProceedingJoinPoint pjp) throws Throwable {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
		String ip = IpUtil.toIpAddr(request);
		String uri = request.getRequestURI();

		// 获取请求参数
		Object[] args = pjp.getArgs();
		List<Object> list = new ArrayList<>();
		for (Object o : args) {
			if (!(o instanceof ServletRequest || o instanceof ServletResponse || o instanceof HttpSession)) {
				list.add(o);
			}
		}
		// 获取session中的用户、HeadInfo等信息
		Enumeration<String> attributeNames = request.getSession().getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String name = attributeNames.nextElement();
			Object obj = request.getSession().getAttribute(name);
			if (obj != null)
				list.add(obj);
		}
		try {
			Object result = pjp.proceed(args);
			log.debug("{},IP:{},Param:{},Result:{}", uri, ip, JSON.toJSONString(list),
					JSON.toJSONString(result, SerializerFeature.WriteNullStringAsEmpty));
			return result;
		} catch (BusinessException e) {
			log.debug("{},IP:{},Param:{},Result:{}", uri, ip, JSON.toJSONString(list),
					e.getCode() + "," + e.getMessage());
			throw e;
		} catch (Exception e) {
			log.debug("{},IP:{},Param:{},Result:{}", uri, ip, JSON.toJSONString(list),
					e.getClass() + ":" + e.getMessage());
			throw e;
		}
	}

	/**
	 * 对请求参数使用oval进行校验
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("aspectService()")
	public Object validatorParams(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		List<List<ConstraintViolation>> list = Lists.newArrayList();
		for (Object obj : args) {
			if (obj == null) {
				throw new BusinessException("请求参数不能为(null)!");
			}
			List<ConstraintViolation> ret = new Validator().validate(obj);
			if (ret != null && ret.size() > 0) {
				list.add(ret);
			}
		}

		if (list.size() > 0) {
			StringBuilder buf = new StringBuilder();
			for (List<ConstraintViolation> lt : list) {
				for (int i = 0; i < lt.size(); i++) {
					buf.append(i == 0 ? "" : ",");
					buf.append(lt.get(i).getMessage());
				}
			}
			throw new BusinessException(buf.toString());
		}
		return pjp.proceed(args);
	}

}
