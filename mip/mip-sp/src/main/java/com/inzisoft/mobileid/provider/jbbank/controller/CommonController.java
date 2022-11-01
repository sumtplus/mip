package com.inzisoft.mobileid.provider.jbbank.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inzisoft.mobileid.provider.jbbank.service.CommonService;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseData;
import com.inzisoft.mobileid.sp.common.dto.response.CommonResponse;
import com.inzisoft.mobileid.sp.common.dto.response.RestResponse;
import com.inzisoft.mobileid.sp.domain.mip.dto.request.RequestTransactionResult;

/**
 * 전북은행 커스텀 컨트롤러
 * @author jhmin
 *
 */
@RestController
// $$ uri 수정필요
@RequestMapping("/v1/mip/common")
public class CommonController {
	private final CommonService commonService;
	
	public CommonController(CommonService commonService) {
		this.commonService = commonService;
	}

	/**
	 * 모바일 신분증 거래상태 및 처리결과 요청
	 * @author jhmin
	 */
	@PostMapping(value = "/bizResult",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RestResponse bizResult(@Valid @RequestBody RequestTransactionResult requestTransactionResult) throws IOException{
		ResponseData data = commonService.getBizResult(requestTransactionResult.getTrxcode()); 
		return CommonResponse.create(data);
	}
}
