package com.inzisoft.mobileid.sp.domain.admin.service;

import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.sp.common.util.UserUtil;
import com.inzisoft.mobileid.sp.domain.admin.dto.AddAdminParam;
import com.inzisoft.mobileid.sp.domain.admin.dto.AdminData;
import com.inzisoft.mobileid.sp.domain.admin.dto.AdminFindParam;
import com.inzisoft.mobileid.sp.domain.admin.dto.UpdateAdminParam;
import com.inzisoft.mobileid.sp.domain.admin.repository.Admin;
import com.inzisoft.mobileid.sp.domain.admin.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class AdminServiceImpl implements AdminService {
	private AdminRepository adminRepository;

	public AdminServiceImpl(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	@Transactional
	public AdminData addAdmin(AddAdminParam addAdminParam) {
		// TODO 사용자 추가할 수 있는 권한인지 확인
		if (adminRepository.findById(addAdminParam.getEmployeeNumber()).isPresent()) {
			throw new CommonException(MipBizError.RESOURCE_ERROR, addAdminParam.getEmployeeNumber());
		}
		return AdminData.from(adminRepository.save(addAdminParam.createAdmin()));
	}

	public AdminData getAdmin(String employeeNumber) {
		return AdminData.from(findById(employeeNumber));
	}

	private Admin findById(String employeeNumber) {
		return adminRepository.findById(employeeNumber).orElseThrow(() ->
				new CommonException(CommonError.NOT_FOUND, employeeNumber));
	}

	@Transactional
	public AdminData updateAdmin(String employeeNumber, UpdateAdminParam updateAdminParam) {
		// TODO 사용자 수정할 수 있는 권한인지 확인
		Admin admin = findById(employeeNumber);
		admin.getDateInfos().setUpdateDateTime();
		admin.getModifiedBy().setUpdateEmployeeNumber(UserUtil.getCurrentLoginUser().getUsername());
		if (com.inzisoft.mobileid.common.util.CommonUtil.isNotNullAndEmpty(updateAdminParam.getEmployeeName())) {
			admin.setEmployeeName(updateAdminParam.getEmployeeName());
		}
		if (com.inzisoft.mobileid.common.util.CommonUtil.isNotNullAndEmpty(updateAdminParam.getPassword())) {
			admin.setPassword(updateAdminParam.getPassword());
			admin.setPasswordChangeDate(LocalDateTime.now());
		}
		if (com.inzisoft.mobileid.common.util.CommonUtil.isNotNullAndEmpty(updateAdminParam.getAuthCode())) {
			admin.setAuthCode(updateAdminParam.getAuthCode());
		}

		if (updateAdminParam.getDeleted() != null) {
			admin.setDeleted(true);
			admin.setDeletedAt(LocalDateTime.now());
		}
		adminRepository.save(admin);
		return AdminData.from(admin);
	}

	/**
	 * 사용하지 않도록 함 (삭제 시 updateAdmin 메소드를 이용하여 Delete flag 만 true로 설정)
	 * @param employeeNumber
	 * @return
	 */
	@Deprecated
	@Transactional
	public AdminData deleteAdmin(String employeeNumber) {
		// TODO 사용자 삭제할 수 있는 권한인지 확인
		Admin admin = findById(employeeNumber);
		adminRepository.delete(admin);
		return AdminData.from(admin);
	}

	/**
	 * 이름은 %{검색어}% 조회
	 * 직원번호는 {검색어}% 조회
	 * 삭제여부는 {검색어} 조회
	 *
	 * @param param
	 * @return
	 */
	public Page<AdminData> findAll(AdminFindParam param, Pageable pageable) {
		Page<Admin> adminPage = adminRepository.findAllByParams(param, pageable);
		return new PageImpl<>(AdminData.from(adminPage.getContent()), pageable, adminPage.getTotalElements());
	}

	@Override
	public int countAdminByAuthCode(String authCode) {
		return adminRepository.countByAuthCodeAndDeletedFalse(authCode);
	}
	
	/**
	 * 기존 비밀번호와 새 비밀번호가 다른지 확인
	 * @return pwConfirmOk : 새 비밀번호가 기존 비밀번호와 다를때
	 * @return pwConfirmNo : 새 비밀번호가 기존 비밀번호와 같을때
	 */
	@Transactional
	public String checkPassword(String newPassword) {
		String result = null;
		
		User user = (User) UserUtil.getCurrentLoginUser();
		Admin admin = adminRepository.findById(user.getUsername()).get();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if(!encoder.matches(newPassword, admin.getPassword())) {
			admin.setPassword(newPassword);
			admin.setPasswordChangeDate(LocalDateTime.now());
			result = "pwConfirmOk";
		} else {
			result = "pwConfirmNo";
		}
		return result;
	}
}
