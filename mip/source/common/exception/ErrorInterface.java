package com.inzisoft.mobileid.common.exception;

public interface ErrorInterface {
	String OK = "0";
	static boolean isSuccess(String code) {
		return OK.equalsIgnoreCase(code);
	}
	static boolean isError(String code) {
		return !isSuccess(code);
	}
	String getCode();
}
