package com.inzisoft.mobileid.sdk.mip.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.inzisoft.mobileid.common.util.PatternJsonParser;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sdk.mip.message.MipMessage;
import com.inzisoft.mobileid.sdk.mip.request.MipRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * MipRequest {
 *     "data":"...base64 encoded MipMessage..."
 * }
 */
@Slf4j
public class MipRequestDeserializer extends JsonDeserializer<MipRequest> {
	private static final String JSON_KEY_DATA = "data";
	private static final String JSON_KEY_TRXCODE = "trxcode";

	@Override
	public MipRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		String encodedMessage = getMessage(jsonParser);
		String jsonMessage = decodeMessage(encodedMessage);

		ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
		return new MipRequest(jsonToMessage(objectMapper, jsonMessage));
	}

	private String getMessage(JsonParser jsonParser) {
		try {
			JsonNode jsonNode = jsonParser.readValueAsTree();
			JsonNode dataNode = jsonNode.get(JSON_KEY_DATA);
			if (dataNode == null) {
				throw new MipException(MipError.SP_UNEXPECTED_MSG_FORMAT, JSON_KEY_DATA);
			}
			return dataNode.textValue();
		} catch (IOException e) {
			throw new MipException(MipError.SP_UNEXPECTED_MSG_FORMAT);
		}
	}

	private String decodeMessage(String encodedMessage) {
		try {
			return new String(Base64.getUrlDecoder().decode(encodedMessage), StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			throw new MipException(MipError.SP_BASE64_DECODE_ERROR);
		}
	}

	private MipMessage jsonToMessage(ObjectMapper objectMapper, String jsonMessage) throws IOException {
		try {
			return objectMapper.readValue(jsonMessage, MipMessage.class);
		} catch (InvalidTypeIdException e) { // 키 값이 누락되었을 때 발생
			log.error("e={}, json={}", e.getMessage(), jsonMessage, e);
			throw new MipException(MipError.SP_MISSING_MANDATORY_ITEM, getTrxCode(jsonMessage), e.getTypeId());
		} catch (JsonProcessingException e) {
			log.error("e={}, json={}", e.getMessage(), jsonMessage, e);
			throw new MipException(MipError.SP_UNEXPECTED_MSG_FORMAT, getTrxCode(jsonMessage), "");
		}
	}

	private String getTrxCode(String jsonMessage) {
		try {
			return PatternJsonParser.getStringValue(jsonMessage, JSON_KEY_TRXCODE);
		} catch (Exception e) {
			return null;
		}
	}
}
