package com.inzisoft.mobileid.sp.common.dto.response;

import com.inzisoft.mobileid.common.exception.ErrorInterface;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse implements RestResponse {
	private String code;
	private String message;

	public ErrorResponse(ErrorInterface error, String message) {
		this.code = error.getCode();
		this.message = message;
	}
}
