package com.inzisoft.mobileid.sp.domain.mip.dto.request;

import com.inzisoft.mobileid.sdk.mip.dto.TransactionOpenInfo;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@SuperBuilder
@ToString
public class TransactionOpenMoreInfo extends TransactionOpenInfo {
	private String channelCode;

	private String deviceId;
	private String branchCode;
	private String branchName;

	private String employeeNumber;
	private String employeeName;

	protected Map<String, String> metadata;
}
