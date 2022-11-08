package com.inzisoft.mobileid.sp.domain.code.dto;

import com.inzisoft.mobileid.sp.domain.code.repository.MipCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
public class CodeData {
	private String groupCode;
	private String code;
	private String name;
	private String description;
	private boolean enabled;

	public static CodeData from(MipCode code) {
		return CodeData.builder()
				.groupCode(code.getId().getGroupCode())
				.code(code.getId().getCode())
				.name(code.getName())
				.description(code.getDescription())
				.enabled(code.isEnabled())
				.build();
	}

	public static List<CodeData> from(List<MipCode> codeList) {
		return codeList.stream()
				.map(CodeData::from)
				.collect(Collectors.toList());
	}
}
