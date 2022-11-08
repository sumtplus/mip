package com.inzisoft.mobileid.sdk.code;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Step {
	UNKNOWN("UNKNOWN"),
	OPEN_TRANSACTION("OPEN_TRANSACTION"),
	ISSUE_PROFILE("ISSUE_PROFILE"),
	VP_VERIFY("VP_VERIFY"),
	COMPLETE("COMPLETE")
	;
	private String code;
	Step(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	@JsonCreator
	public static Step fromCode(String code) {
		return Arrays.stream(values())
				.filter(step -> step.code.equalsIgnoreCase(code))
				.findFirst()
				.orElse(UNKNOWN);
	}
}
