package com.inzisoft.mobileid.sp.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class UserUtil {
	public static User getCurrentLoginUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (securityContext != null) {
			Authentication authentication = securityContext.getAuthentication();
			if (authentication != null) {
				Object principal = authentication.getPrincipal();
				if (principal != null && principal instanceof User) {
					return (User) principal;
				}
			}
		}
		return null;
	}
}
