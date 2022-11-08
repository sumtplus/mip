package com.inzisoft.mobileid.sp.domain.code.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AddCodeParam {
	private String groupCode;
	private String code;
	private String name;
	private String description;
	private boolean enabled = true;
	private String employeeNumber;
}
