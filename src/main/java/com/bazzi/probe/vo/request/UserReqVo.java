package com.bazzi.probe.vo.request;

import lombok.Data;
import net.sf.oval.constraint.NotNull;

@Data
public class UserReqVo {
	@NotNull(message = "id不能为空")
	private Long id;

}
