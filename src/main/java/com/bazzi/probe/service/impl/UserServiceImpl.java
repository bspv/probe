package com.bazzi.probe.service.impl;

import com.bazzi.probe.dao.UserDao;
import com.bazzi.probe.model.User;
import com.bazzi.probe.service.UserService;
import com.bazzi.probe.vo.request.UserReqVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;

	public User findUserById(UserReqVo userReqVo) {
		return userDao.selectByPrimaryKey(userReqVo.getId());
	}
}
