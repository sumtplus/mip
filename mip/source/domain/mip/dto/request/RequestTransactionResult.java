package com.inzisoft.mobileid.sp.domain.mip.dto.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
public class RequestTransactionResult {
    @NotEmpty
    private String trxcode;
}
