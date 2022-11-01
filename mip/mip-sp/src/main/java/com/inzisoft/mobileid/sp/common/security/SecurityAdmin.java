package com.inzisoft.mobileid.sp.common.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.inzisoft.mobileid.sp.domain.admin.repository.Admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SecurityAdmin extends User {

	private static final long serialVersionUID = 1L;
	
	private Admin admin;
	
	public SecurityAdmin(Admin admin) {
		super(admin.getEmployeeNumber(), admin.getPassword(), !admin.isDeleted(), true, true, true, AuthorityUtils.createAuthorityList("ROLE_" + admin.getAuthCode().toString()));
		
		this.admin = admin;
	}
	
}
