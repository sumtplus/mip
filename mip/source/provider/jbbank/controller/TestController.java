package com.inzisoft.mobileid.provider.jbbank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.inzisoft.mobileid.provider.jbbank.service.CommonService;
import com.inzisoft.mobileid.provider.jbbank.service.FileTestService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {
	private WebClient webClient;
	
	private FileTestService fileTestService;
	
	private CommonService commonService;

	public TestController(WebClient webClient, FileTestService fileTestService, CommonService commonService) {
		this.webClient = webClient;
		this.fileTestService = fileTestService;
		this.commonService = commonService;
	}

	@GetMapping("/reqStatus")
	@ResponseBody
	public Mono<String> reqStatus(String trxcode) throws Exception{
		return webClient.mutate()
				.baseUrl("http://localhost:8080")
				.build()
				.get()
				.uri("/v1/mip/biz/status?trxcode=" + trxcode)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class);
	}
	
	@GetMapping("/reqStatusPost")
	@ResponseBody
	public Mono<String> reqStatusPost() throws Exception{
		Gson gson = new Gson();
		String trxcode = "sample";
		
		return webClient.mutate()
				.baseUrl("http://localhost:8080")
				.build()
				.post()
				.uri("/v1/mip/biz/status")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(trxcode)
				.retrieve()
				.bodyToMono(String.class);
	}
	
	@PostMapping("/uploadFile")
	@ResponseBody
//	public String uploadFile(MultipartHttpServletRequest request) throws Exception{
	public void uploadFile(MultipartHttpServletRequest request) throws Exception{
		String ret = "";
		String docIrn = fileTestService.uploadFile(request);
		System.out.println(docIrn);
//		if(!ObjectUtils.isEmpty(docIrn)) {
//			ret = HttpStatus.OK + "-" + docIrn;
//		}else {
//			ret = HttpStatus.BAD_REQUEST.toString();
//		}
		
//		return ret;
	}
	
	@GetMapping("/mdlSend")
	@ResponseBody
	public String mdlSend(String trxcode) throws Exception{
		commonService.sendMdlImage(trxcode, null);
		return "complete";
	}
}
