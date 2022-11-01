package com.inzisoft.mobileid.sp.domain.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
public class TransactionHistoryData {
	private String transactionCode;
	private String serviceCode;
	private String submitMode;
	private String interfaceType;
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
	public static TransactionHistoryData from(MipTransactionHistory mipTransactionHistory) {
		return TransactionHistoryData.builder()
				.transactionCode(mipTransactionHistory.getCode())
				.serviceCode(mipTransactionHistory.getMipServiceInfo().getCode())
				.submitMode(mipTransactionHistory.getSubmitMode().getCode())
				.interfaceType(mipTransactionHistory.getInterfaceType() == null? null: mipTransactionHistory.getInterfaceType().name())
				.channelCode(mipTransactionHistory.getChannelCode())
				.nonce(mipTransactionHistory.getNonce())
				.stepCode(mipTransactionHistory.getStep().name())
				.resultCode(mipTransactionHistory.getResultCode())
				.idType(mipTransactionHistory.getIdType() == null? null: mipTransactionHistory.getIdType())
				.createdAt(mipTransactionHistory.getDateInfos().getCreateDateTime())
				.updatedAt(mipTransactionHistory.getDateInfos().getUpdateDateTime())
				.branchCode(mipTransactionHistory.getBranchCode())
				.branchName(mipTransactionHistory.getBranchName())
				.employeeNo(mipTransactionHistory.getEmployeeNumber())
				.employeeName(mipTransactionHistory.getEmployeeName())
				.customerName(mipTransactionHistory.getCustomerName())
				.customerBirth(mipTransactionHistory.getCustomerBirth())
				.vpArchiveInfo(mipTransactionHistory.getVpArchiveInfo())
				.idArchiveInfo(mipTransactionHistory.getIdArchiveInfo())
				.metadata(mipTransactionHistory.getMetadata())
				.build();
	}

	public static List<TransactionHistoryData> from(List<MipTransactionHistory> mipTransactionHistories) {
		return mipTransactionHistories.stream()
				.map(TransactionHistoryData::from)
				.collect(Collectors.toList());
	}
}
