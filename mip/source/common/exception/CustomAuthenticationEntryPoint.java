package com.inzisoft.mobileid.sp.domain.security.service;

import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.util.JacksonUtil;
import com.inzisoft.mobileid.sp.common.dto.response.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
	public CustomAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException, ServletException {
		if (request.getMethod().equals("POST")
				&& request.getHeader("content-type").equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			ErrorResponse errorResponse = new ErrorResponse(CommonError.UNAUTHORIZED, "Unauthorized");
			response.getOutputStream().print(JacksonUtil.getJson(errorResponse));
			return;
		}
		super.commence(request, response, ex);
	}
}