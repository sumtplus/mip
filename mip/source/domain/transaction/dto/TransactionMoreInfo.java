package com.inzisoft.mobileid.sp.domain.transaction.dto;

import com.inzisoft.mobileid.sdk.mip.dto.TransactionInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@ToString
public class TransactionMoreInfo extends TransactionInfo {
	private String channelCode;

	private String idType;

	private String branchCode;
	private String branchName;
	private String employeeNumber;
	private String employeeName;
	private String deviceId;

	private Map<String, String> metadata;
}
