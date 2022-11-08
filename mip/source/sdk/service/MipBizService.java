package com.inzisoft.mobileid.sdk.service;

import java.io.IOException;

import com.inzisoft.mobileid.sdk.image.model.ImageType;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionOpenInfo;
import com.inzisoft.mobileid.sdk.mip.message.MipMessage;
import com.inzisoft.mobileid.sdk.mip.response.ResultProfile;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseData;

public interface MipBizService {
    ResponseData openTransaction(TransactionOpenInfo transactionOpenInfo);
    ResultProfile getProfile(MipMessage message);
    void verifyVp(MipMessage message);
    ResponseData getTransactionStatus(String trxcode);
    byte[] getImage(String trxcode, ImageType fromCode, String format) throws IOException;
    ResponseData getPrivacy(String trxcode) throws IOException;
	void handleError(MipMessage message);
}
