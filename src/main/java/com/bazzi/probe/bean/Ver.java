package com.bazzi.probe.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ver implements Serializable {
	private static final long serialVersionUID = -1549796387131216866L;
	private int idx;
	private int pageSize;
	private String cur;

}
