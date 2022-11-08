package com.inzisoft.mobileid.sp.domain.admin.dto;

import com.inzisoft.mobileid.common.util.CommonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AdminFindParam {
	private String employeeName;
	private String employeeNumber;
	private boolean deleted;

	public void setEmployeeNumber(String employeeNumber) {
		if(CommonUtil.isNullOrEmpty(employeeNumber)){
			this.employeeNumber = null;
		}else{
			this.employeeNumber = employeeNumber;
		}
	}

	public void setEmployeeName(String employeeName) {
		if(CommonUtil.isNullOrEmpty(employeeName)){
			this.employeeName = null;
		}else{
			this.employeeName = employeeName;
		}
	}
}
