package com.bazzi.probe.test.features;

import com.bazzi.probe.model.User;
import com.bazzi.probe.service.UserService;
import com.bazzi.probe.test.TestBase;
import com.bazzi.probe.vo.request.UserReqVo;
import org.junit.Test;

import javax.annotation.Resource;

public class TestUserService extends TestBase {
	@Resource
	private UserService userService;

	@Test
	public void testLoad(){
		UserReqVo userReqVo = new UserReqVo();
		userReqVo.setId(1L);
		User user = userService.findUserById(userReqVo);
		print(user);
	}

}
