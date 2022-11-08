package com.inzisoft.mobileid.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtil {
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static String getJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			String errorMessage = String.format("getJson failed, object=%s, e=%s", object.toString(), e.getMessage());
			log.error(errorMessage);
			throw new CommonException(CommonError.JSON_ERROR, errorMessage);
		}
	}

	public static <T> T getObject(String json, Class<T> classType) throws IOException {
		try {
			return objectMapper.readValue(json, classType);
		} catch (JsonProcessingException e) {
			String errorMessage = String.format("getObject failed, class=%s, json=%s, e=%s",
					classType.getSimpleName(), json, e.getMessage());
			log.error(errorMessage);
			throw new CommonException(CommonError.JSON_ERROR, errorMessage);
		}
	}

	public static <T> T getObject(String json, TypeReference<T> valueTypeRef) throws IOException {
		try {
			return objectMapper.readValue(json, valueTypeRef);
		} catch (JsonProcessingException e) {
			String errorMessage = String.format("getObject failed, class=%s, json=%s, e=%s",
					valueTypeRef.getType().getTypeName(), json, e.getMessage());
			log.error(errorMessage);
			throw new CommonException(CommonError.JSON_ERROR, errorMessage);
		}
	}
}
