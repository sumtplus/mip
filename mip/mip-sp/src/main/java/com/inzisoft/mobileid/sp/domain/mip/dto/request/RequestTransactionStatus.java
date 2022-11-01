package com.inzisoft.mobileid.sp.domain.mip.dto.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@ToString
public class RequestTransactionStatus {
    @NotEmpty
    private String trxcode;
}
