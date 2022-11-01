package com.inzisoft.mobileid.sp.common.model.converter;

import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.common.util.JacksonUtil;

import javax.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {
	@Override
	public String convertToDatabaseColumn(Map<String, Object> map) {
		return JacksonUtil.getJson(map);
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String json){
		if (json == null) {
			return null;
		}
		if (json.isEmpty()) {
			return new HashMap<>();
		}
		try {
			return JacksonUtil.getObject(json, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
