package com.inzisoft.mobileid.sp.domain.mip.controller;

import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sdk.mip.message.MessageFactory;
import com.inzisoft.mobileid.sdk.mip.message.MipMessage;
import com.inzisoft.mobileid.sdk.mip.request.MipRequest;
import com.inzisoft.mobileid.sdk.mip.response.MipResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@Slf4j
@RestControllerAdvice(assignableTypes  = MipController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MipControllerAdvice extends RequestBodyAdviceAdapter {
	private static final String MESSAGE_KEY = "MIP_MESSAGE";

	@ExceptionHandler(MipException.class)
	public MipResponse mipExceptionHandler(MipException e) {
		log.error("Mip Exception e={}", e.getMessage(), e);

		try {
			MipMessage message = loadMessage();
			return MipResponse.failed(MessageFactory.getFactory(message)
					.createErrorMessage(message.getTrxcode(), e.getError()));
		} catch (Exception ex) {
			log.warn("No message saved");
		}
		return MipResponse.failed(MessageFactory.getFactory().createErrorMessage(e));
	}

	@ExceptionHandler(DataAccessException.class)
	public MipResponse dataAccessExceptionHandler(DataAccessException e) {
		log.error("DataAccessException e={}", e.getMessage(), e);
		try {
			MipMessage message = loadMessage();
			return MipResponse.failed(MessageFactory.getFactory(message)
					.createErrorMessage(message.getTrxcode(), MipError.SP_DB_ERROR));
		} catch (Exception ex) {
			log.warn("No message saved");
		}
		return MipResponse.failed(MessageFactory.getFactory()
				.createErrorMessage(null, MipError.SP_DB_ERROR));
	}

	@ExceptionHandler(Exception.class)
	public MipResponse exceptionHandler(Exception e) {
		log.error("Exception e={}", e.getMessage(), e);
		try {
			MipMessage message = loadMessage();
			return MipResponse.failed(MessageFactory.getFactory(message)
					.createErrorMessage(message.getTrxcode(), MipError.UNKNOWN_ERROR));
		} catch (Exception ex) {
			log.warn("No message saved");
		}
		return MipResponse.failed(MessageFactory.getFactory()
				.createErrorMessage(null, MipError.UNKNOWN_ERROR));
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		log.debug("afterBodyRead {}", Thread.currentThread().getId());

		saveMessage(body);
		return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
		return true;
	}

	private void saveMessage(Object body) {
		if (body != null && body instanceof MipRequest) {
			RequestContextHolder.currentRequestAttributes().setAttribute(MESSAGE_KEY, ((MipRequest) body).getMessage(), RequestAttributes.SCOPE_REQUEST);
		}
	}

	private MipMessage loadMessage() {
		Object message = RequestContextHolder.currentRequestAttributes().getAttribute(MESSAGE_KEY, RequestAttributes.SCOPE_REQUEST);
		if (message == null || !(message instanceof MipMessage)) {
			throw new IllegalStateException("No message");
		}
		return (MipMessage) message;
	}
}
