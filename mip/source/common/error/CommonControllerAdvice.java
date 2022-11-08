package com.inzisoft.mobileid.sp.common.error;

import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sp.common.dto.response.ErrorResponse;
import com.inzisoft.mobileid.sp.common.dto.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class CommonControllerAdvice {
	@ExceptionHandler(MipException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public RestResponse handleMipException(MipException e) {
		log.error("MipException e={}", e.getMessage(), e);
		return new ErrorResponse(e.getError(), e.getMessage());
	}

	@ExceptionHandler(CommonException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public RestResponse commonExceptionHandler(CommonException e) {
		log.error("CommonException e={}", e.getMessage(), e);
		return new ErrorResponse(e.getError(), e.getMessage());
	}

	@ExceptionHandler(DataAccessException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public RestResponse dataAccessExceptionHandler(DataAccessException e) {
		log.error("DataAccessException e={}", e.getMessage(), e);
		return new ErrorResponse(MipError.SP_DB_ERROR, "Error while data handling");
	}

	@ExceptionHandler(HttpMessageConversionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RestResponse httpMessageConversionExceptionHandler(HttpMessageConversionException e) {
		log.error("HttpMessageConversionException e={}", e.getMessage(), e);
		return new ErrorResponse(MipBizError.BAD_REQUEST, "Bad request");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RestResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException e={}", e.getMessage(), e);
		String invalidFields = e.getBindingResult().getFieldErrors()
				.stream()
				.map(fieldError ->
						String.format("%s=%s", fieldError.getField(), fieldError.getRejectedValue()))
				.collect(Collectors.joining(","));
		return new ErrorResponse(MipBizError.BAD_REQUEST, invalidFields);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RestResponse exceptionHandler(Exception e) {
		log.error("Exception e={}", e.getMessage(), e);
		return new ErrorResponse(MipBizError.INTERNAL_SERVER_ERROR, e.getMessage());
	}
}
