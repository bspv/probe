package com.bazzi.probe.controller;

import com.bazzi.core.result.Result;
import com.bazzi.probe.model.User;
import com.bazzi.probe.service.UserService;
import com.bazzi.probe.vo.request.UserReqVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class UserController {
	@Resource
	private UserService userService;

	@RequestMapping(value = "userIdx" ,method = {RequestMethod.POST,RequestMethod.GET})
	public String userIdx(ModelMap modelMap){
		UserReqVo userReqVo = new UserReqVo();
		userReqVo.setId(1L);
		modelMap.put("user",userService.findUserById(userReqVo));
		return "user/userIdx";
	}

	@ResponseBody
	@RequestMapping(value = "/userAjax", method = { RequestMethod.POST, RequestMethod.GET })
	public Result<User> userAjax(@RequestParam Long idx) {
		Result<User> result = new Result<>();
		UserReqVo userReqVo = new UserReqVo();
		userReqVo.setId(idx);
		result.setData(userService.findUserById(userReqVo));
		return result;
	}
}
