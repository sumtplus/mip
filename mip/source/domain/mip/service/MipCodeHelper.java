package com.inzisoft.mobileid.sp.domain.mip.service;

import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sp.domain.code.dto.CodeData;
import com.inzisoft.mobileid.sp.domain.code.service.CodeService;

public class MipCodeHelper {
	private CodeService codeService;

	private static String GROUP_CODE_ERROR_CODE = "ERROR_CODE";

	private String getErrorMessage(MipError mipError) {
		CodeData codeData = codeService.getCode(GROUP_CODE_ERROR_CODE, mipError.getCode());
		return codeData.getDescription();
	}

}
