package com.inzisoft.mobileid.sp.common.security;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.inzisoft.mobileid.sp.domain.admin.repository.Admin;
import com.inzisoft.mobileid.sp.domain.admin.repository.AdminRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoginProcessHandler implements AuthenticationSuccessHandler {
	
	private static final long passwordDateLimit = 30L;
	private final AdminRepository adminRepository;

	public LoginProcessHandler(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}


	@Transactional
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SecurityAdmin securityAdmin = (SecurityAdmin)auth.getPrincipal();
		Admin admin = adminRepository.findById(securityAdmin.getAdmin().getEmployeeNumber()).get();
		admin.setRecentLogin(LocalDateTime.now());
		
		try {
			// 비밀번호 만료일 체크
			checkPasswordDateLimit(request, response, admin);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	public void checkPasswordDateLimit(HttpServletRequest request, HttpServletResponse response, Admin admin) throws IOException, ParseException {
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate today = LocalDate.now();
		LocalDate changedDate;
		
		if (admin.getPasswordChangeDate() == null) { // 계정 생성 후, 비밀번호 변경 이력이 없는 경우
			changedDate = LocalDate.parse(admin.getDateInfos().getCreateDateTime().substring(0, 8), dateTimeFormatter);
		} else {
			changedDate = LocalDate.parse(admin.getPasswordChangeDate(), dateTimeFormatter);
		}
		
		long days = ChronoUnit.DAYS.between(changedDate, today);
//		System.out.println("today = " + today + ", changedDate = " + changedDate);
//		System.out.println(days);
		
		if (days >= passwordDateLimit) {
			response.sendRedirect("pwchange");
		} else {
			response.sendRedirect("tran");
		}
		
	}

}
