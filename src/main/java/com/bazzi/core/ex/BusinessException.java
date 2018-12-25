package com.bazzi.core.ex;

import org.springframework.beans.BeanUtils;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 5516360419962257393L;
	private String code;
	private String message;

	public BusinessException() {
	}

	public BusinessException(String message) {
		this("-1", message);
	}

	public BusinessException(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public <T> BusinessException(T t) {
		if (t != null)
			BeanUtils.copyProperties(t, this);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
