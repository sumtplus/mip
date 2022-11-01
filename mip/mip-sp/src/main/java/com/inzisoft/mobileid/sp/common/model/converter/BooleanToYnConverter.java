package com.inzisoft.mobileid.sp.common.model.converter;

import com.inzisoft.mobileid.common.util.CommonUtil;

import javax.persistence.AttributeConverter;

public class BooleanToYnConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean yn) {
		return yn == null? null:
				yn ? "Y": "N";
	}

	@Override
	public Boolean convertToEntityAttribute(String yn) {
		return CommonUtil.isNullOrEmpty(yn)? null:
				Boolean.parseBoolean(yn)? Boolean.TRUE: Boolean.FALSE;
	}
}
