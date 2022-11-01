package com.inzisoft.mobileid.sp.common.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Embeddable
public class DateInfos {
	@Column(name = "create_dttm")
	private String createDateTime;
	@Column(name = "update_dttm")
	private String updateDateTime;
	@Transient
	private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@PrePersist
	public void setCreateDateTime() {
		createDateTime = DATE_TIME_FORMATTER.format(LocalDateTime.now());
		updateDateTime = DATE_TIME_FORMATTER.format(LocalDateTime.now());
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	@PreUpdate
	public void setUpdateDateTime() {
		updateDateTime = DATE_TIME_FORMATTER.format(LocalDateTime.now());
	}

	public String getUpdateDateTime() {
		return updateDateTime;
	}
}
