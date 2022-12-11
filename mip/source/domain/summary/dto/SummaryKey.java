package com.inzisoft.mobileid.sp.domain.summary.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SummaryKey implements Serializable {
    private String employeeNumber;
    private String trxDate;
}
