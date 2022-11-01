package com.inzisoft.mobileid.sp.domain.mip.service;

import com.inzisoft.mobileid.sdk.code.IdType;
import com.inzisoft.mobileid.sdk.code.Step;
import com.inzisoft.mobileid.sdk.code.SubmitMode;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.generator.TransactionCodeGenerator;
import com.inzisoft.mobileid.sdk.mip.dto.ServiceInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionOpenInfo;
import com.inzisoft.mobileid.sdk.service.MipServiceHelper;
import com.inzisoft.mobileid.sp.domain.mip.dto.request.TransactionOpenMoreInfo;
import com.inzisoft.mobileid.sp.domain.service.repository.MipServiceInfo;
import com.inzisoft.mobileid.sp.domain.service.service.MipServiceInfoService;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionData;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionMoreInfo;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class MipServiceHelperImpl implements MipServiceHelper {
	private final MipServiceInfoService mipServiceInfoService;
	private final MipTransactionService mipTransactionService;
	private final TransactionCodeGenerator transactionCodeGenerator;

	public MipServiceHelperImpl(MipServiceInfoService mipServiceInfoService,
								MipTransactionService mipTransactionService,
								TransactionCodeGenerator transactionCodeGenerator) {
		this.mipServiceInfoService = mipServiceInfoService;
		this.mipTransactionService = mipTransactionService;
		this.transactionCodeGenerator = transactionCodeGenerator;
	}

	@Override
	public String generateTrxcode(TransactionOpenInfo openInfo) {
		return transactionCodeGenerator.generate(openInfo);
	}

	@Override
	public Optional<ServiceInfo> getServiceInfo(String serviceCode) {
		Optional<MipServiceInfo> optional = mipServiceInfoService.getService(serviceCode);
		if (!optional.isPresent()){
			return Optional.empty();
		}
		return Optional.of(createServiceInfo(optional.get()));
	}

	private ServiceInfo createServiceInfo(MipServiceInfo mipServiceInfo) {
		return ServiceInfo.builder()
				.code(mipServiceInfo.getCode())
				.name(mipServiceInfo.getName())
				.providerName(mipServiceInfo.getProviderName())
				.callbackUrl(mipServiceInfo.getCallbackUrl())
				.presentType(mipServiceInfo.getPresentType())
				.authTypes(mipServiceInfo.getAuthType())
				.zkpAttributes(mipServiceInfo.getZkpAttributes())
				.zkpPredicates(mipServiceInfo.getZkpPredicates())
				.build();
	}

	@Override
	public boolean isExistTrxCode(String trxcode) {
		return mipTransactionService.get(trxcode).isPresent();
	}

	@Override
	public Optional<TransactionInfo> getTransactionInfo(String trxcode) {
		Optional<TransactionData> optional = mipTransactionService.get(trxcode);
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		TransactionData trxData = optional.get();
		return Optional.of(TransactionInfo.builder()
				.nonce(trxData.getNonce())
				.submitMode(SubmitMode.fromCode(trxData.getSubmitMode()))
				.transactionCode(trxData.getTransactionCode())
				.serviceCode(trxData.getServiceCode())
				.step(Step.fromCode(trxData.getStepCode()))
				.resultCode(trxData.getResultCode())
				.vpArchiveInfo(trxData.getVpArchiveInfo())
				.createdAt(trxData.getCreatedAt())
				.updatedAt(trxData.getUpdatedAt())
				.build());
	}

	@Override
	public TransactionInfo createTransactionInfo(String transactionCode, TransactionOpenInfo openInfo) {
		TransactionOpenMoreInfo openMoreInfo = (TransactionOpenMoreInfo) openInfo;
		return TransactionMoreInfo.builder()
				.transactionCode(transactionCode)
				.serviceCode(openInfo.getServiceCode())
				.interfaceType(openInfo.getInterfaceType())
				.submitMode(openInfo.getSubmitMode())
				.step(Step.OPEN_TRANSACTION)
				.resultCode(MipError.OK.getCode())
				.channelCode(openMoreInfo.getChannelCode())
				.branchCode(openMoreInfo.getBranchCode())
				.branchName(openMoreInfo.getBranchName())
				.deviceId(openMoreInfo.getDeviceId())
				.employeeNumber(openMoreInfo.getEmployeeNumber())
				.employeeName(openMoreInfo.getEmployeeName())
				.metadata(openMoreInfo.getMetadata())
				// TODO 현재는 무조건 IdType 운전면허증으로 저장하도록 함
				.idType(IdType.DRIVING_LICENSE.getCode())  
				.build();
	}
}
