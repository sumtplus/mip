package com.inzisoft.mobileid.sp.domain.admin.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateAdminParam {
	private String password;
	private String employeeName;
	private String authCode;
	private Boolean deleted;

	public static UpdateAdminParam createDeleteParam() {
		UpdateAdminParam updateAdminParam = new UpdateAdminParam();
		updateAdminParam.deleted = true;
		return updateAdminParam;
	}
}
