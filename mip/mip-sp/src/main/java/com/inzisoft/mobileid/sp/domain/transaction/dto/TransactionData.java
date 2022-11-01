package com.inzisoft.mobileid.sp.domain.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransaction;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
public class TransactionData {
	private String transactionCode;
	private String serviceCode;
	private String interfaceType;
	private String submitMode;
	private String channelCode;
	@JsonIgnore
	private String nonce;
	private String stepCode;
	private String resultCode;
	private String idType;
	private String createdAt;
	private String updatedAt;
	private String branchCode;
	private String branchName;
	private String employeeNo;
	private String employeeName;
	private String deviceId;
	private String customerName;
	private String customerBirth;
	private String vpArchiveInfo;
	private String idArchiveInfo;
	private Map<String, String> metadata;

	public static TransactionData from(MipTransaction mipTransaction) {
		return TransactionData.builder()
				.transactionCode(mipTransaction.getCode())
				.serviceCode(mipTransaction.getMipServiceInfo().getCode())
				.interfaceType(mipTransaction.getInterfaceType() == null? null: mipTransaction.getInterfaceType().name())
				.submitMode(mipTransaction.getSubmitMode().getCode())
				.channelCode(mipTransaction.getChannelCode())
				.nonce(mipTransaction.getNonce())
				.stepCode(mipTransaction.getStep().name())
				.resultCode(mipTransaction.getResultCode())
				.idType(mipTransaction.getIdType() == null? null: mipTransaction.getIdType())
				.createdAt(mipTransaction.getDateInfos().getCreateDateTime())
				.updatedAt(mipTransaction.getDateInfos().getUpdateDateTime())
				.branchCode(mipTransaction.getBranchCode())
				.branchName(mipTransaction.getBranchName())
				.employeeNo(mipTransaction.getEmployeeNumber())
				.employeeName(mipTransaction.getEmployeeName())
				.deviceId(mipTransaction.getDeviceId())
				.customerName(mipTransaction.getCustomerName())
				.customerBirth(mipTransaction.getCustomerBirth())
				.vpArchiveInfo(mipTransaction.getVpArchiveInfo())
				.idArchiveInfo(mipTransaction.getIdArchiveInfo())
				.metadata(mipTransaction.getMetadata())
				.build();
	}

	public static List<TransactionData> from(List<MipTransaction> mipTransactions) {
		return mipTransactions.stream()
				.map(TransactionData::from)
				.collect(Collectors.toList());
	}
}
