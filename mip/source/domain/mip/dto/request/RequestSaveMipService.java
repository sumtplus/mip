package com.inzisoft.mobileid.sp.domain.mip.dto.request;

import com.inzisoft.mobileid.sdk.code.AuthType;
import com.inzisoft.mobileid.sdk.code.DrivingLicenseZkpVc;
import com.inzisoft.mobileid.sdk.code.PresentType;
import com.inzisoft.mobileid.sp.domain.service.repository.MipServiceInfo;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestSaveMipService {
	private String code;
	private String name;

	private List<AuthType> authTypes;
	private PresentType presentType;
	private String providerName;

	private String zkpPredicates;
	private List<DrivingLicenseZkpVc> zkpAttributes;

	public MipServiceInfo createMipService() {
		return MipServiceInfo.builder()
				.code(code)
				.name(name)
				.authType(authTypes)
				.presentType(presentType)
				.providerName(providerName)
				.zkpAttributes(zkpAttributes)
				.zkpPredicates(zkpPredicates)
				.build();
	}
}
