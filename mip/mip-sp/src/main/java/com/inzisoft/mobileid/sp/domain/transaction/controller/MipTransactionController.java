package com.inzisoft.mobileid.sp.domain.transaction.controller;

import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionFindParam;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/transactions")
public class MipTransactionController {
	private MipTransactionService mipTransactionService;

	public MipTransactionController(MipTransactionService mipTransactionService) {
		this.mipTransactionService = mipTransactionService;
	}

	@GetMapping(value = "/{trxcode}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity get(@PathVariable("trxcode") String trxcode) {
		Optional optional = mipTransactionService.get(trxcode);
		if (!optional.isPresent()) {
			throw new CommonException(CommonError.NOT_FOUND, trxcode);
		}
		return ResponseEntity.ok(optional.get());
	}

	@GetMapping(value = "",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity find(@ModelAttribute TransactionFindParam param,
							   @PageableDefault(size = 10, sort = "create_dttm", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(mipTransactionService.find(param, pageable));
	}

	@GetMapping(value = "/download")
	public void getExcelDownload(@ModelAttribute TransactionFindParam param,@PageableDefault(size = Integer.MAX_VALUE, sort = "create_dttm", direction = Sort.Direction.DESC) Pageable pageable,
								 HttpServletRequest request, HttpServletResponse response){
		mipTransactionService.downloadExcelTransaction(param, pageable,request, response);
	}

}
