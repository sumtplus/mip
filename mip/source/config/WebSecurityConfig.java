package com.inzisoft.mobileid.config;

import com.inzisoft.mobileid.sp.common.model.converter.PasswordConverter;
import com.inzisoft.mobileid.sp.common.security.LoginProcessHandler;
import com.inzisoft.mobileid.sp.common.security.SecurityAdminServiceImpl;
import com.inzisoft.mobileid.sp.domain.admin.repository.AdminRepository;
import com.inzisoft.mobileid.sp.domain.security.service.CustomAuthenticationEntryPoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final DataSource dataSource;
	private final LoginProcessHandler loginProcessHandler;


	private static final String LOGIN_FORM_URL = "/login";

	public WebSecurityConfig(DataSource dataSource, LoginProcessHandler loginProcessHandler) {
		this.dataSource = dataSource;
		this.loginProcessHandler = loginProcessHandler;
	}

	@Autowired
	private SecurityAdminServiceImpl securityAdminServiceImpl;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.headers()
				.frameOptions().sameOrigin()
				.and()
			.authorizeRequests()
				.antMatchers("/css/**", "/images/**", "/js/**", "/fonts/**").permitAll()
				.antMatchers("/favicon.ico").permitAll()
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers("/swagger*/**", "/v3/api-docs/**").permitAll() // Swagger 보안해제
				.antMatchers("/v1/mip/biz/**", "/mip/**").permitAll() // API 보안해제
				//jhmin 0526
				.antMatchers("/v1/mip/common/**", "/mip/**").permitAll() // 커스텀 API 보안 해제
				//jhmin 0527
				.antMatchers("/test/**", "/mip/**").permitAll() // 테스트 API 보안 해제
				.antMatchers("/v1/api/admins").permitAll()
				//.hasRole("A")
				.antMatchers("/v1/api/summary/**").hasRole("A")
				.antMatchers("/error").permitAll()
				.antMatchers("/tran").hasAnyRole("A", "U")
				.antMatchers("/stat").hasRole("A")
				.antMatchers("/user").hasRole("A")
				.antMatchers("/passwordChange").hasAnyRole("A", "U")
				.anyRequest().authenticated()
				.and()

			.csrf().disable() // test용도
			.exceptionHandling()
				// 보안 적용된 API 호출 시 POST/Json 요청은 Json으로 Response 그 외는 redirect
				.authenticationEntryPoint(authenticationEntryPoint())
				.and()

			.headers()
				.addHeaderWriter(
					new XFrameOptionsHeaderWriter(
						new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))
					)
				)
				.frameOptions().sameOrigin()
			.and()
				.formLogin()
				.loginPage(LOGIN_FORM_URL)
				.successHandler(loginProcessHandler)
				.permitAll()
				.and()
			.logout()
				.permitAll();

	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(securityAdminServiceImpl);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// JPA에서 사용되는 Converter 는 자동으로 빈 생성되나 PasswordConverter 의
	// static 멤버로 passwordEncoder를 설정해주기 위해서 임의로 생성
	@Bean
	public PasswordConverter passwordConverter(PasswordEncoder passwordEncoder) {
		PasswordConverter passwordConverter = new PasswordConverter();
		passwordConverter.setPasswordEncoder(passwordEncoder);
		return passwordConverter;
	}

	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint(LOGIN_FORM_URL);
	}
}
