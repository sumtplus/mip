package com.inzisoft.mobileid.sp.domain.transaction.dto;

import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.common.util.LocalDateUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
public class TransactionFindParam {
	private String idType;
	private String branchName;
	private String employeeNumber;
	private String customerName;
	private String channelCode;
	@NotEmpty
	private String startDate; // yyyyMMdd
	@NotEmpty
	private String endDate; // yyyyMMdd

	public void setIdType(String idType) {
		if(CommonUtil.isNullOrEmpty(idType)){
			this.idType = null;
		}else{
			this.idType = idType;
		}
	}

	public void setBranchName(String branchName) {
		if(CommonUtil.isNullOrEmpty(branchName)){
			this.branchName = null;
		}else{
			this.branchName = branchName;
		}
	}

	public void setChannelCode(String channelCode) {
		if(CommonUtil.isNullOrEmpty(channelCode)){
			this.channelCode = null;
		}else{
			this.channelCode = channelCode;
		}
	}

	public void setCustomerName(String customerName) {
		if(CommonUtil.isNullOrEmpty(customerName)){
			this.customerName = null;
		}else{
			this.customerName = customerName;
		}
	}

	public void setEmployeeNumber(String employeeNumber) {
		if(CommonUtil.isNullOrEmpty(employeeNumber)){
			this.employeeNumber = null;
		}else{
			this.employeeNumber = employeeNumber;
		}
	}

	public void setStartDate(String date) {
		this.startDate = LocalDateUtil.toDateTimeString(LocalDateTime.of(Integer.parseInt(date.substring(0, 4)),
				Integer.parseInt(date.substring(4, 6)),
				Integer.parseInt(date.substring(6, 8)),
				0, 0, 0));
	}

	public void setEndDate(String date) {
		this.endDate = LocalDateUtil.toDateTimeString(LocalDateTime.of(Integer.parseInt(date.substring(0, 4)),
				Integer.parseInt(date.substring(4, 6)),
				Integer.parseInt(date.substring(6, 8)),
				23, 59, 59));
	}


}
