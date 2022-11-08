package com.inzisoft.mobileid.sp.common.security;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.inzisoft.mobileid.sp.domain.admin.repository.Admin;
import com.inzisoft.mobileid.sp.domain.admin.repository.AdminRepository;

@Component
public class SecurityAdminServiceImpl implements UserDetailsService {
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<Admin> user = adminRepository.findById(username);
		
		if(!user.isPresent()) {
			throw new UsernameNotFoundException("사용자 없음");
		}
		
		Admin admin = user.get();


		return new SecurityAdmin(admin);
	}
	

}
