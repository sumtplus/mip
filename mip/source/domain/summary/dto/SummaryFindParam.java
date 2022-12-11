package com.inzisoft.mobileid.sp.domain.summary.dto;

import com.inzisoft.mobileid.common.util.CommonUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SummaryFindParam {
    private String idType;
    private String branchName;
    private String employeeNumber;
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
    public void setEmployeeNumber(String employeeNumber) {
        if(CommonUtil.isNullOrEmpty(employeeNumber)){
            this.employeeNumber = null;
        }else{
            this.employeeNumber = employeeNumber;
        }
    }


}
