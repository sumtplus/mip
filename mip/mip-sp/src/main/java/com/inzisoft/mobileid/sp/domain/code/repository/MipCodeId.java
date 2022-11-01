package com.inzisoft.mobileid.sp.domain.code.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@Embeddable
public class MipCodeId implements Serializable {
	@Column(name = "group_code")
	private String groupCode;
	private String code;

	private MipCodeId(String groupCode, String code) {
		this.groupCode = groupCode;
		this.code = code;
	}

	public static MipCodeId create(String groupCode, String code) {
		return new MipCodeId(groupCode, code);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MipCodeId mipCodeId = (MipCodeId) o;
		return groupCode.equals(mipCodeId.groupCode) && code.equals(mipCodeId.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupCode, code);
	}
}
