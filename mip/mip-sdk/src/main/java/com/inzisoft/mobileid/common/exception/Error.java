package com.inzisoft.mobileid.common.exception;

public interface Error {
	static boolean isSuccess(String code) {
		return "0".equalsIgnoreCase(code);
	}
	static boolean isError(String code) {
		return !isSuccess(code);
	}
	String getCode();
}
