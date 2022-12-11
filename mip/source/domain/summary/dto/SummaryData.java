package com.inzisoft.mobileid.sp.domain.summary.dto;

import com.inzisoft.mobileid.sp.domain.summary.repository.Summary;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
public class SummaryData {
	private String employeeName;
	private String employeeNumber;
	private String trxDate;
	private String idType;
	private String branchCode;
	private String branchName;
	private String txOpenCount;
	private String vpVerifyCount;
	private String completeCount;


	public static SummaryData from(Summary summary) {
		return SummaryData.builder()
				.employeeName(summary.getEmployeeName())
				.employeeNumber(summary.getEmployeeNumber())
				.trxDate(summary.getTrxDate())
				.idType(summary.getIdType()== null? null: summary.getIdType())
				.branchCode(summary.getBranchCode())
				.branchName(summary.getBranchName())
				.txOpenCount(summary.getTxOpenCount())
				.vpVerifyCount(summary.getVpVerifyCount())
				.completeCount(summary.getCompleteCount())
				.build();
	}

	public static List<SummaryData> from(List<Summary> summaryList) {
		return summaryList.stream()
				.map(SummaryData::from)
				.collect(Collectors.toList());
	}
}
