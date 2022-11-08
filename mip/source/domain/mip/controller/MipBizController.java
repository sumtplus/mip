package com.inzisoft.mobileid.sp.domain.mip.controller;

import com.inzisoft.mobileid.sdk.image.model.ImageType;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseData;
import com.inzisoft.mobileid.sdk.service.MipBizService;
import com.inzisoft.mobileid.sp.common.dto.response.CommonResponse;
import com.inzisoft.mobileid.sp.common.dto.response.RestResponse;
import com.inzisoft.mobileid.sp.domain.mip.dto.request.RequestGetImage;
import com.inzisoft.mobileid.sp.domain.mip.dto.request.RequestOpenTransaction;
import com.inzisoft.mobileid.sp.domain.mip.dto.request.RequestTransactionResult;
import com.inzisoft.mobileid.sp.domain.mip.dto.request.RequestTransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/mip/biz")
@Slf4j
public class MipBizController {
	private final MipBizService mipBizService;

	public MipBizController(MipBizService mipBizService) {
		this.mipBizService = mipBizService;
	}

	@PostMapping(value = "/open",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public RestResponse openTransaction(@Valid @RequestBody RequestOpenTransaction request) {
		request.validate();

		ResponseData data = mipBizService.openTransaction(request.createTransactionOpenInfo());
		return CommonResponse.create(data);
	}

	@GetMapping(value = "/status",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RestResponse status(@RequestParam("trxcode") String trxcode) {
		ResponseData data = mipBizService.getTransactionStatus(trxcode);
		return CommonResponse.create(data);
	}

	@PostMapping(value = "/status",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RestResponse status(@Valid @RequestBody RequestTransactionStatus requestTransactionStatus) {
		ResponseData data = mipBizService.getTransactionStatus(requestTransactionStatus.getTrxcode());
		return CommonResponse.create(data);
	}

	@GetMapping(value = "/result",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RestResponse getResult(@RequestParam("trxcode") String trxcode) throws IOException {
		ResponseData data = mipBizService.getPrivacy(trxcode);
		return CommonResponse.create(data);
	}

	@PostMapping(value = "/result",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RestResponse getResult(@Valid @RequestBody RequestTransactionResult requestTransactionResult) throws IOException {
		ResponseData data = mipBizService.getPrivacy(requestTransactionResult.getTrxcode());
		return CommonResponse.create(data);
	}

	@GetMapping(value = "/images",
		produces = { MediaType.IMAGE_JPEG_VALUE })
	public ResponseEntity<Resource> getImages(@RequestParam("trxcode") String trxcode,
											 @RequestParam("type") String type,
											 @RequestParam(value = "format", defaultValue = "jpeg") String format) throws IOException {
		byte[] bytes = mipBizService.getImage(trxcode, ImageType.fromCode(type), format);
		return new ResponseEntity<>(new ByteArrayResource(bytes), HttpStatus.OK);
	}

	@PostMapping(value = "/images",
			produces = { MediaType.IMAGE_JPEG_VALUE })
	public ResponseEntity<Resource> getImages(@Valid @RequestBody RequestGetImage request) throws IOException {
		byte[] bytes = mipBizService.getImage(request.getTrxcode(), ImageType.fromCode(request.getType()), request.getFormat());
		return new ResponseEntity<>(new ByteArrayResource(bytes), HttpStatus.OK);
	}
}
