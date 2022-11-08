package com.inzisoft.mobileid.common.exception;

public enum CommonError implements ErrorInterface {
	UNKNOWN_ERROR,
	NOT_FOUND,
	JSON_ERROR,
	INVALID_INPUT,
	CONFLICT,
	DATA_CONFLICT,
	BAD_REQUEST,
	INVALID_STEP,
	ALREADY_EXIST,
	FILE_ERROR,
	INVALID_GRANT,
	CREATE_TRXCODE,

	UNAUTHORIZED;

	@Override
	public String getCode() {
		return name();
	}
}
