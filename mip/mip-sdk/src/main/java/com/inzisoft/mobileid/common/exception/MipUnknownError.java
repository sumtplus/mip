package com.inzisoft.mobileid.sdk.code.error;

import com.inzisoft.mobileid.common.exception.ErrorInterface;

public class MipUnknownError implements ErrorInterface {
	String code;
	String message;

	public MipUnknownError(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public MipUnknownError(int code, String message) {
		this.code = String.valueOf(code);
		this.message = message;
	}

	@Override
	public String getCode() {
		return code;
	}
}
