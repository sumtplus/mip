package com.inzisoft.mobileid.sp.domain.transaction.repository;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.inzisoft.mobileid.sdk.code.InterfaceType;
import com.inzisoft.mobileid.sdk.code.Step;
import com.inzisoft.mobileid.sdk.code.SubmitMode;
import com.inzisoft.mobileid.sp.common.model.DateInfos;
import com.inzisoft.mobileid.sp.common.model.converter.MapToJsonConverter;
import com.inzisoft.mobileid.sp.domain.service.repository.MipServiceInfo;
import lombok.*;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "mip_trx_history")
public class MipTransactionHistory {
	@Id @Column(nullable = false, updatable = false,
			name = "trx_code")
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_code",
			foreignKey = @ForeignKey(name = "fk_transaction_service_code"),
			referencedColumnName= "service_code", updatable = false)
	@JsonBackReference
	private MipServiceInfo mipServiceInfo;

	@Column(name = "interface_type")
	@Enumerated(EnumType.STRING)
	private InterfaceType interfaceType;

	@Column(name = "submit_mode")
	@Enumerated(EnumType.STRING)
	private SubmitMode submitMode;

	@Column(name = "channel_code")
	private String channelCode;

	@Column(name = "nonce")
	private String nonce;

	@Column(name = "step_code")
	@Enumerated(EnumType.STRING)
	private Step step;

	@Column(name = "result_code")
	private String resultCode;

	@Column(name ="id_type")
	private String idType;

	@Column(name = "device_id")
	private String deviceId;

	@Column(name = "branch_code")
	private String branchCode;

	@Column(name = "branch_name")
	private String branchName;

	@Column(name = "employee_no")
	private String employeeNumber;

	@Column(name = "employee_name")
	private String employeeName;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "customer_birth")
	private String customerBirth;

	@Builder.Default
	@Embedded
	private DateInfos dateInfos = new DateInfos();

	@Column(name = "vp_archive_info")
	private String vpArchiveInfo;

	@Column(name = "id_archive_info")
	private String idArchiveInfo;

	@SuppressWarnings("JpaAttributeTypeInspection") // Using type Map warning
	@Column(name = "metadata")
	@Convert(converter = MapToJsonConverter.class)
	private Map<String, String> metadata;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MipTransactionHistory that = (MipTransactionHistory) o;
		return code.equals(that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
}