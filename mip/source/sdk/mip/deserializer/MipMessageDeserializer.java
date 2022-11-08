package com.inzisoft.mobileid.sdk.mip.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sdk.code.Cmd;
import com.inzisoft.mobileid.sdk.mip.message.MessageFactory;
import com.inzisoft.mobileid.sdk.mip.message.MipMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Json 형태 MipMessage 를 MipMessage 상속받은 구현체로 Deserialize
 *
 * Json의 cmd 값으로 Cmd를 가져오고 Cmd에 정의된 구현체 클래스로 Deserialize 진행
 * cmd 에 정의되지 않은 메시지의 경우 UnknownMessage 로 변환되어 저장
 */
@Slf4j
public class MipMessageDeserializer extends JsonDeserializer<MipMessage> {
	private final MessageFactory messageFactory;

	public MipMessageDeserializer(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	public MipMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		String trxcode = getValue(node, "trxcode", null);
		try {
			ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();

			Cmd cmd = Cmd.fromCode(getValue(node, "cmd", trxcode));
			String version = getValue(node, "version", trxcode);
			String type = getValue(node, "type", trxcode);

			MipMessage message = objectMapper.treeToValue(node, MessageFactory.getFactory(type, version).getMessageClass(cmd));
			message.validate();

			return message;
		} catch (MismatchedInputException e) {
			log.error("missing creator, e={}", e.getMessage(), e);
			throw new MipException(MipError.SP_MISSING_MANDATORY_ITEM, trxcode, e.getPath().get(0).getFieldName());
		}
	}

	private String getValue(JsonNode root, String itemName, String trxcode) {
		JsonNode node = root.get(itemName);
		if (node == null || node.getNodeType() != JsonNodeType.STRING) {
			throw new MipException(MipError.SP_MISSING_MANDATORY_ITEM, trxcode, itemName);
		}
		String value = node.textValue();
		if (value == null || value.isEmpty()) {
			throw new MipException(MipError.SP_MISSING_MANDATORY_ITEM, trxcode, itemName);
		}
		return value;
	}
}
