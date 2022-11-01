package com.inzisoft.mobileid.sp.domain.admin.service;

import com.inzisoft.mobileid.sp.domain.admin.dto.AddAdminParam;
import com.inzisoft.mobileid.sp.domain.admin.dto.AdminData;
import com.inzisoft.mobileid.sp.domain.admin.dto.AdminFindParam;
import com.inzisoft.mobileid.sp.domain.admin.dto.UpdateAdminParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface AdminService {
	AdminData addAdmin(AddAdminParam addAdminParam);
	AdminData getAdmin(String employeeNumber);
	AdminData updateAdmin(String employeeNumber, UpdateAdminParam updateAdminParam);
	AdminData deleteAdmin(String employeeNumber);
	Page<AdminData> findAll(AdminFindParam param, Pageable pageable);
	int countAdminByAuthCode(String authCode);
	String checkPassword(String newPassword);
}
