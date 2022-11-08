package com.inzisoft.mobileid.sp.domain.admin.repository;

import com.inzisoft.mobileid.sp.common.model.DateInfos;
import com.inzisoft.mobileid.sp.common.model.ModifyEmployee;
import com.inzisoft.mobileid.sp.common.model.converter.PasswordConverter;
import com.inzisoft.mobileid.sp.common.model.converter.BooleanToYnConverter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Setter
@Getter
@DynamicUpdate
@Table(name = "mip_admin")
public class Admin {
	@Transient
	private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	@Transient
	private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Id @Column(name = "employee_no")
	private String employeeNumber;

	@Convert(converter = PasswordConverter.class)
	private String password;
	private String employeeName;
	private String authCode;
	@Column(name = "recent_login_dttm")
	private String recentLogin;
	@Column(name = "password_change_date")
	private String passwordChangeDate;
	@Embedded
	private ModifyEmployee modifiedBy;
	@Embedded
	private DateInfos dateInfos = new DateInfos();
	@Column(name = "delete_dttm")
	private String deletedAt;
	@Convert(converter = BooleanToYnConverter.class)
	@Column(name = "delete_yn")
	private boolean deleted;

	public void setRecentLogin(LocalDateTime localDateTime) {
		this.recentLogin = DATE_TIME_FORMATTER.format(localDateTime);
	}

	public void setPasswordChangeDate(LocalDateTime localDateTime) {
		this.passwordChangeDate =  DATE_FORMATTER.format(localDateTime);
	}

	public void setDeletedAt(LocalDateTime localDateTime) {
		this.deletedAt = DATE_TIME_FORMATTER.format(localDateTime);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Admin admin = (Admin) o;
		return Objects.equals(employeeNumber, admin.employeeNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(employeeNumber);
	}
}
