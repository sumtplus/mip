package com.inzisoft.mobileid.sp.domain.view.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("")
@Slf4j
public class WebController {
	
	/*
	 * 로그인 페이지 호출
	 */
	@GetMapping("/login")
	public String login() {
		log.info("로그인페이지 ");
		return "views/common/login";
	}
	
	/*
	 * 거래 현황 조회 페이지 호출
	 */
	@GetMapping("/tran")
	public String transactionList() {
		log.info("거래 현황 조회 페이지");
		return "views/transaction/transactionList";
	}
	
	@GetMapping("")
	public String home() {
		return "redirect:tran";
	}
	
	/*
	 * 통계 조회 페이지 호출
	 */
	@GetMapping("/stat")
	public String statisticsList() {
		log.info("통계 조회 페이지");
		return "views/statistics/statisticsList";
	}
	
	/*
	 * 사용자 관리 페이지 호출
	 */
	@GetMapping("/user")
	public String userList() {
		log.info("사용자 관리 페이지");
		return "views/user/userList";
	}
	
	/*
	 * 비밀번호 변경 페이지 호출
	 */
	@GetMapping("/pwchange")
	public String passwordChange() {
		log.info("비밀번호 변경 페이지");
		return "views/common/passwordChange";
	}
	 
}
