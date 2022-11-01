package com.inzisoft.mobileid.sp.domain.admin.dto;

import com.inzisoft.mobileid.sp.domain.admin.repository.Admin;
import com.inzisoft.mobileid.sp.common.model.ModifyEmployee;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class AddAdminParam {
	@NotEmpty
	private String employeeNumber;
	@NotEmpty
	private String password;
	@NotEmpty
	private String employeeName;
	@NotEmpty
	private String authCode;
	@NotEmpty
	private String createBy;

	public Admin createAdmin() {
		Admin admin = new Admin();
		admin.setEmployeeNumber(employeeNumber);
		admin.setEmployeeName(employeeName);
		admin.setAuthCode(authCode);
		admin.setPassword(password);
		admin.setModifiedBy(ModifyEmployee.builder()
				.createEmployeeNumber(createBy).updateEmployeeNumber(createBy)
				.build());
		admin.setDeleted(false);
		return admin;
	}
}
