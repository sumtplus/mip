package com.inzisoft.mobileid.sdk.mip.exception;

import com.inzisoft.mobileid.common.exception.ErrorInterface;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MipException extends RuntimeException {
	private ErrorInterface error;
	private String transactionCode;
	private String reason;

	public MipException(MipError error) {
		super(String.format("errorCode: %s, message: %s",
				error.getCode(), error.getMessage()));
		this.error = error;
	}

	public MipException(MipError error, String transactionCode, String reason) {
		super(String.format("errorCode: %s, message: %s, trxcode: %s, reason: %s",
				error.getCode(), error.getMessage(), transactionCode, reason));
		this.error = error;
		this.transactionCode = transactionCode;
		this.reason = reason;
	}

	public MipException(MipError error, String reason) {
		super(String.format("errorCode: %s, message: %s, reason: %s",
				error.getCode(), error.getMessage(), reason));
		this.error = error;
		this.reason = reason;
	}

	public MipException(MipError error, String reason, Throwable e) {
		super(String.format("errorCode: %s, message: %s, reason: %s",
				error.getCode(), error.getMessage(), reason), e);
		this.error = error;
		this.reason = reason;
	}

	public MipException(MipBizError mipBizError, String reason, Throwable e) {
		super(String.format("errorCode: %s, reason: %s",
				mipBizError.getCode(), reason), e);
		this.error = MipError.UNKNOWN_ERROR;
		this.reason = reason;
	}
}
