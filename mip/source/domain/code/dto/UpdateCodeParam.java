package com.inzisoft.mobileid.sp.domain.code.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class UpdateCodeParam {
	private String name;
	private String description;
	private Boolean enabled;
	@Setter
	private String employeeNumber;
}
