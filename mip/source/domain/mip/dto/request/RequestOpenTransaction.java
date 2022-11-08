package com.inzisoft.mobileid.sp.domain.mip.dto.request;

import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.sdk.code.InterfaceType;
import com.inzisoft.mobileid.sdk.code.SubmitMode;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionOpenInfo;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Getter
@ToString
public class RequestOpenTransaction {
	private String transactionCode;
	@NotEmpty
	private String serviceCode;
	private InterfaceType interfaceType = InterfaceType.QR_MPM;
	private SubmitMode submitMode = SubmitMode.DIRECT;
	private String channelCode;

	private String deviceId;
	private String branchCode;
	private String branchName;
	private String employeeNumber;
	private String employeeName;

	private Map<String, String> metadata;

	private boolean includeQrCode = false;
	private boolean includeProfile = false;

	public TransactionOpenInfo createTransactionOpenInfo() {
		return TransactionOpenMoreInfo.builder()
				.transactionCode(transactionCode)
				.serviceCode(serviceCode)
				.interfaceType(interfaceType)
				.submitMode(submitMode)
				.channelCode(channelCode)
				.deviceId(deviceId)
				.branchCode(branchCode)
				.branchName(branchName)
				.employeeNumber(employeeNumber)
				.employeeName(employeeName)
				.includeQrCode(includeQrCode)
				.includeProfile(includeProfile)
				.metadata(metadata)
				.build();
	}

	public void validate() {
		if (interfaceType != InterfaceType.QR_MPM && interfaceType != InterfaceType.APP2APP) {
			throw new CommonException(MipBizError.BAD_REQUEST);
		}

		validateAdditionalInfos();
	}

	public void validateAdditionalInfos() {
		if (interfaceType == InterfaceType.QR_MPM) {
			if (CommonUtil.isNullOrEmpty(branchCode, branchName, employeeNumber, employeeName)) {
				throw new CommonException(MipBizError.BAD_REQUEST, "Request Branch, Employee infos required");
			}
		}
	}
}
