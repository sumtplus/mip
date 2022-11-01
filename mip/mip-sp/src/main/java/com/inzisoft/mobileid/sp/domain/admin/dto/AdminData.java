package com.inzisoft.mobileid.sp.domain.admin.dto;

import com.inzisoft.mobileid.sp.domain.admin.repository.Admin;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
public class AdminData {
	private String employeeName;
	private String employeeNumber;
	private String authCode;
	private String recentLoginAt;
	private String createdBy;
	private String createdByNm;
	private String createdAt;
	private String updatedBy;
	private String updatedByNm;
	private String updatedAt;
	private boolean deleted;
	private String deletedAt;
	private String passwordChangeDate;

	public static AdminData from(Admin admin) {
		return AdminData.builder()
				.employeeName(admin.getEmployeeName())
				.employeeNumber(admin.getEmployeeNumber())
				.authCode(admin.getAuthCode())
				.recentLoginAt(admin.getRecentLogin())
				.createdAt(admin.getDateInfos().getCreateDateTime())
				.createdBy(admin.getModifiedBy().getCreateEmployeeNumber())
				.createdByNm(admin.getModifiedBy().getCreateEmployeeInfo()  == null? null: admin.getModifiedBy().getCreateEmployeeInfo().getEmployeeName()  )
				.updatedAt(admin.getDateInfos().getUpdateDateTime())
				.updatedBy(admin.getModifiedBy().getUpdateEmployeeNumber())
				.updatedByNm(admin.getModifiedBy().getUpdateEmployeeInfo() == null? null: admin.getModifiedBy().getUpdateEmployeeInfo().getEmployeeName())
				.passwordChangeDate(admin.getPasswordChangeDate())
				.deleted(admin.isDeleted())
				.deletedAt(admin.getDeletedAt())
				.build();
	}

	public static List<AdminData> from(List<Admin> adminList) {
		return adminList.stream()
				.map(AdminData::from)
				.collect(Collectors.toList());
	}
}
