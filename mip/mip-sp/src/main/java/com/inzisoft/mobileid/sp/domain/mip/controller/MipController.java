package com.inzisoft.mobileid.sp.domain.mip.controller;

import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.config.SpConfig;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sdk.mip.message.MipMessage;
import com.inzisoft.mobileid.sdk.mip.message.impl.ErrorMessage;
import com.inzisoft.mobileid.sdk.mip.message.impl.RequestImageMessage;
import com.inzisoft.mobileid.sdk.mip.message.impl.VpSubmitMessage;
import com.inzisoft.mobileid.sdk.mip.request.MipRequest;
import com.inzisoft.mobileid.sdk.mip.response.MipResponse;
import com.inzisoft.mobileid.sdk.mip.response.ResponseProfile;
import com.inzisoft.mobileid.sdk.mip.response.ResultProfile;
import com.inzisoft.mobileid.sdk.service.MipBizService;
import com.raonsecure.omnione.core.exception.IWException;
import com.raonsecure.omnione.core.util.http.HttpException;
import com.raonsecure.omnione.sdk_server_core.blockchain.common.BlockChainException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mip")
@Api(value = "메시지 규격 요청 controller")
@Slf4j
public class MipController {
	private final SpConfig spConfig;

	private final MipBizService mipBizService;

	public MipController(SpConfig spConfig, MipBizService mipBizService) {
		this.spConfig = spConfig;
		this.mipBizService = mipBizService;
	}

	/**
	 * M310 Profile 요청 처리
	 *
	 * @param request { "data":"base64로 인코딩된 M310 메시지" }
	 * @return { "result":true, "data":"base64로 인코딩된 응답 메시지" }
	 * @throws Exception
	 */
	@PostMapping(value = "/profile")
	@ApiOperation(value = "Profile 요청", notes = "M310 메시지를 전송하여 Profile을 요청한다.")
	public MipResponse requestProfile(@RequestBody MipRequest request) throws IWException, BlockChainException, HttpException {
		MipMessage message = request.getMessage();
		// Validation check (URL에 따라 들어와야 하는 메시지 타입이 정해져 있음)

		ResultProfile profile = mipBizService.getProfile(message);
		return MipResponse.success(ResponseProfile.create(message.getTrxcode(), profile.getJsonProfile()));
	}

	/**
	 * M320 메시지 처리
	 * 모바일앱에서 요청을 하며, 요청시에 이미지 데이터(M320) 을 리턴한다.
	 * 이미지의 경우
	 * 현재 구현에서는 단순히 application.properties 에 지정되어 있는 이미지 파일의
	 * 이미지 파일의 경로를 읽어서 byte[] 를 base64 인코딩해서 전달한다.
	 * <p>
	 * 본 API 는 단순 예시로 어떤 이미지를 전달할지는 각 SP 의 상황에 따라서 처리한다.
	 *
	 * @param request
	 * @return MipResponseData
	 */
	@PostMapping(value = "/image")
	@ApiOperation(value = "BI 이미지 요청", notes = "M320 메시지를 전송하여 BI 이미지를 요청한다.")
	public MipResponse requestBiImage(@RequestBody MipRequest request) {
		RequestImageMessage message = (RequestImageMessage) request.getMessage();

		// TODO 검증 필요 시 Service 거치도록 함
		// Service 를 거친다면 이미지를 서비스마다 다르게 하는 것도 가능할듯

		// 이미지의 경우
		// 현재 구현에서는 이미지 파일을 byte[]로 읽어서 이를 base64 인코딩해서 전달한다.
		// 좀 던 단순화 하기 위해 미리 base64로 변환해 놓은 문자열을 상수로 이용한다.
		// 본 API 는 단순 예시로 어떤 이미지를 전달할지는 각 SP 의 상황에 따라서 처리한다.
		final String BASE64_IMAGE = spConfig.getSpImageBase64();
		return MipResponse.success(() -> BASE64_IMAGE);
	}

	/**
	 * M400 VP 검증 메시지 처리
	 *
	 * @param request MipRequestData
	 * @return MipResponseData
	 */
	@ResponseBody
	@PostMapping(value = "/vp")
	@ApiOperation(value = "VP 제출", notes = "M400 메시지를 전송하여 VP를 제출한다.")
	public MipResponse submitVp(@RequestBody MipRequest request) {
		MipMessage message = request.getMessage();
		if (!(message instanceof VpSubmitMessage)) {
			throw new MipException(MipError.SP_INVALID_CMD, "cmd");
		}
		log.debug(message.toString());

		mipBizService.verifyVp(message);
		return MipResponse.success();
	}

	/**
	 * M900 오류 메시지 처리
	 *
	 * @param request { "data":"base64로 인코딩된 오류 메시지" }
	 * @return { "result":true }
	 */
	@ResponseBody
	@PostMapping(value = "/error")
	@ApiOperation(value = "오류 제출", notes = "M900 메시지를 전송하여 오류를 제출한다.")
	public MipResponse submitError(@RequestBody MipRequest request) {
		MipMessage message = request.getMessage();
		log.info("Error received: {}", message);

		if (!(message instanceof ErrorMessage)) {
			throw new MipException(MipError.SP_INVALID_CMD, "cmd");
		}
		try {
			// 본 샘플코드에서는 M900(Error 메시지) 수신 시 별도 처리를 하지 않음
			// 서비스용 구현 시에는 해당 거래코드에 대해 오류 처리하고 DB에 정보 변경 필요
			mipBizService.handleError(message);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		// 오류 여부와 관계없이 { "result":true } 를 응답한다.
		return MipResponse.success();
	}
}
