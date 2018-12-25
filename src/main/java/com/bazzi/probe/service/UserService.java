package com.bazzi.probe.service;

import com.bazzi.probe.model.User;
import com.bazzi.probe.vo.request.UserReqVo;

public interface UserService {
	User findUserById(UserReqVo userReqVo);

}
