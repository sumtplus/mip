package com.inzisoft.mobileid.provider.jbbank.service;

import java.io.IOException;

import com.inzisoft.mobileid.sdk.mip.response.data.ResponseData;

public interface CommonService {
	void sendMdlImage(String trxcode, String vpArchiveInfo) throws IOException;
	
	ResponseData getBizResult(String trxcode) throws IOException; 
}
