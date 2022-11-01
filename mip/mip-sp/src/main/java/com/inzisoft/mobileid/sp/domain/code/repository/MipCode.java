package com.inzisoft.mobileid.sp.domain.code.repository;

import com.inzisoft.mobileid.sp.common.model.DateInfos;
import com.inzisoft.mobileid.sp.common.model.ModifyEmployee;
import com.inzisoft.mobileid.sp.common.model.converter.BooleanToYnConverter;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mip_code")
public class MipCode {
	@EmbeddedId
	private MipCodeId id;
	@Setter
	private String name;
	@Setter
	private String description;
	@Setter
	@Column(name = "enable_yn")
	@Convert(converter = BooleanToYnConverter.class)
	private boolean enabled;
	@Embedded
	private ModifyEmployee modifiedBy;
	@Builder.Default
	@Embedded
	private DateInfos dateInfos = new DateInfos();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MipCode mipCode = (MipCode) o;
		return id.equals(mipCode.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
