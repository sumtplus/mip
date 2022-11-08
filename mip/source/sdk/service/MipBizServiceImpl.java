package com.inzisoft.mobileid.sdk.service.impl;

import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.exception.ErrorInterface;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.common.exception.cause.NotFoundDetail;
import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.sdk.code.*;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.code.error.MipUnknownError;
import com.inzisoft.mobileid.sdk.config.SdkConfig;
import com.inzisoft.mobileid.sdk.event.step.DefaultStepEvent;
import com.inzisoft.mobileid.sdk.event.step.StepEventPublisher;
import com.inzisoft.mobileid.sdk.event.step.value.StepOpenEventValue;
import com.inzisoft.mobileid.sdk.event.step.value.StepProfileEventValue;
import com.inzisoft.mobileid.sdk.event.step.value.StepVerifyEventValue;
import com.inzisoft.mobileid.sdk.image.model.ImageType;
import com.inzisoft.mobileid.sdk.mip.config.SpConfig;
import com.inzisoft.mobileid.sdk.mip.dto.ServiceInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionOpenInfo;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sdk.mip.message.MessageFactory;
import com.inzisoft.mobileid.sdk.mip.message.MipMessage;
import com.inzisoft.mobileid.sdk.mip.message.impl.ErrorMessage;
import com.inzisoft.mobileid.sdk.mip.message.impl.RequestVpMessage;
import com.inzisoft.mobileid.sdk.mip.message.impl.VpSubmitMessage;
import com.inzisoft.mobileid.sdk.mip.response.ResponseRequestVpData;
import com.inzisoft.mobileid.sdk.mip.response.ResultProfile;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseData;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponsePrivacyData;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseTransactionData;
import com.inzisoft.mobileid.sdk.mip.vp.Vp;
import com.inzisoft.mobileid.sdk.mip.vp.VpData;
import com.inzisoft.mobileid.sdk.mip.vp.VpVerifyResult;
import com.inzisoft.mobileid.sdk.service.*;
import com.raonsecure.omnione.core.exception.IWException;
import com.raonsecure.omnione.core.util.http.HttpException;
import com.raonsecure.omnione.sdk_server_core.blockchain.common.BlockChainException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class MipBizServiceImpl implements MipBizService {
	private final SpConfig spConfig;
	private final SdkConfig sdkConfig;
	private final DidService didService;
	private final MipServiceHelper mipServiceHelper;
	private final MessageService messageService;
	private final ImageService imageService;
	private final VpService vpService;
	private final StepEventPublisher stepEventPublisher;

	public MipBizServiceImpl(SpConfig spConfig,
							 SdkConfig sdkConfig,
							 DidService didService,
							 MipServiceHelper mipServiceHelper,
							 MessageService messageService,
							 ImageService imageService,
							 VpService vpService,
							 StepEventPublisher stepEventPublisher) {
		this.spConfig = spConfig;
		this.sdkConfig = sdkConfig;
		this.didService = didService;
		this.mipServiceHelper = mipServiceHelper;
		this.messageService = messageService;
		this.imageService = imageService;
		this.vpService = vpService;
		this.stepEventPublisher = stepEventPublisher;
	}

	@Override
	public ResponseData openTransaction(TransactionOpenInfo openInfo) {
		validateServiceCode(openInfo.getServiceCode());

		// QR-CPM 방식의 경우 Client가 trxcode를 생성
		String transactionCode = CommonUtil.isNotNullAndEmpty(openInfo.getTransactionCode()) ?
				openInfo.getTransactionCode():
				mipServiceHelper.generateTrxcode(openInfo);

		duplicatedTransactionCode(transactionCode);

		TransactionInfo transactionInfo = mipServiceHelper.createTransactionInfo(transactionCode, openInfo);
		if (openInfo.getSubmitMode() == SubmitMode.DIRECT ||
				openInfo.getSubmitMode() == SubmitMode.INDIRECT) {
			return openTransaction(openInfo, transactionInfo);
		}
		if (openInfo.getSubmitMode() == SubmitMode.PROXY) {
			log.error("Proxy protocol not supported");
			// TODO Proxy websocket 방식 추후 구현
		}
		if (openInfo.getSubmitMode() == SubmitMode.P2P) {
			log.error("P2P protocol not supported");
			// TODO P2P는 아마도 지원하지 않을 것으로 예상됨
		}
		throw new CommonException(MipBizError.NOT_SUPPORT_SUBMIT, String.format("%s", openInfo.getSubmitMode()));
	}

	private ResponseData openTransaction(TransactionOpenInfo openInfo, TransactionInfo transactionInfo) {
		MipMessage message = createRequestVpMessage(openInfo, transactionInfo);

		byte[] qrImage = null;
		if (openInfo.isIncludeQrCode() && !openInfo.isIncludeProfile()) {
			qrImage = imageService.createMessageToQr(ImageType.QR, "png", message);
		}

		if (openInfo.isIncludeProfile()) {
			addProfileToMessage(transactionInfo, message);
		}

		return ResponseRequestVpData.builder()
				.trxcode(message.getTrxcode())
				.message(message)
				.qr(qrImage)
				.build();
	}

	private void addProfileToMessage(TransactionInfo transactionInfo, MipMessage message) {
		ResultProfile resultProfile = createProfile(transactionInfo.getTransactionCode(), transactionInfo.getServiceCode());
		if (message instanceof RequestVpMessage) {
			((RequestVpMessage) message).setProfile(resultProfile.getBase64Profile());
		} else {
			throw new MipException(MipError.UNKNOWN_ERROR, "Set profile to message failed");
		}
	}

	private void duplicatedTransactionCode(String transactionCode) {
		if (mipServiceHelper.isExistTrxCode(transactionCode)) {
			throw new MipException(MipError.SP_PROFILE_CREATE_ERROR);
		}
	}

	private ServiceInfo validateServiceCode(String serviceCode) {
		try {
			return getServiceInfo(serviceCode);
		} catch (MipException e) {
			throw new CommonException(MipBizError.SERVICE_NOTFOUND, String.format("serviceCode=%s", serviceCode));
		} catch (RuntimeException e) {
			throw new CommonException(MipBizError.INTERNAL_SERVER_ERROR, String.format("serviceCode=%s", serviceCode), e);
		}
	}

	private MipMessage createRequestVpMessage(TransactionOpenInfo openInfo,
												TransactionInfo transactionInfo) {
		DefaultStepEvent stepEvent = DefaultStepEvent.start(transactionInfo.getTransactionCode(), Step.OPEN_TRANSACTION);
		try {
			MipMessage message = createRequestVpMessage(transactionInfo.getTransactionCode(), openInfo);
			stepEvent.end(MipError.OK, StepOpenEventValue.create(transactionInfo));
			return message;
		} catch (CommonException e) {
			log.error("Create requestVpMessage failed, e={}", e.getMessage());
			stepEvent.end(e.getError());
			throw e;
		} catch (RuntimeException e) {
			log.error("Create requestVpMessage failed, e={}", e.getMessage());
			stepEvent.end(MipBizError.INTERNAL_SERVER_ERROR);
			throw new CommonException(MipBizError.INTERNAL_SERVER_ERROR, e);
		} finally {
			stepEventPublisher.publish(stepEvent);
		}
	}

	private MipMessage createRequestVpMessage(String transactionCode,
													TransactionOpenInfo openInfo) {
		return messageService.createVpRequestMessage(transactionCode,
				spConfig.getSpServer(),
				openInfo.getSubmitMode(),
				spConfig.getSpImageurl(),
				true);
	}

	private ResultProfile createProfile(String transactionCode, String serviceCode) {
		DefaultStepEvent stepEvent = DefaultStepEvent.start(transactionCode, Step.ISSUE_PROFILE);
		try {
			ServiceInfo serviceInfo = getServiceInfo(serviceCode);
			ResultProfile resultProfile = createProfile(serviceInfo);
			stepEvent.end(MipError.OK, StepProfileEventValue.builder()
					.nonce(resultProfile.getNonce())
					.build());
			return resultProfile;
		} catch (MipException e) {
			stepEvent.end(e.getError());
			throw e;
		} catch (RuntimeException e) {
			log.error("Issue profile failed, e={}", e.getMessage());
			stepEvent.end(MipError.UNKNOWN_ERROR);
			throw new MipException(MipError.UNKNOWN_ERROR, "Unknown Error");
		} finally {
			stepEventPublisher.publish(stepEvent);
		}
	}

	@Override
	public ResultProfile getProfile(MipMessage message) {
		if (message.getClass() != MessageFactory.getFactory(message.getType(), message.getVersion())
				.getMessageClass(Cmd.M310)) {
			throw new MipException(MipError.SP_INVALID_CMD, message.getCmd().code);
		}

		TransactionInfo transaction = getTransactionInfo(message.getTrxcode());
		if (transaction.getStep() != Step.OPEN_TRANSACTION
				|| ErrorInterface.isError(transaction.getResultCode())) {
			throw new MipException(MipError.SP_MSG_SEQ_ERROR,
					String.format("%s(%s)", transaction.getStep(), transaction.getResultCode()));
		}
		
		DefaultStepEvent stepEvent = DefaultStepEvent.start(message.getTrxcode(), Step.ISSUE_PROFILE);
		try {
			// TODO QR 생성 시간과 Profile 요청 타임 체크 필요 시 여기서 처리

			ServiceInfo serviceInfo = getServiceInfo(transaction.getServiceCode());
			ResultProfile resultProfile = createProfile(serviceInfo);

			stepEvent.end(MipError.OK, StepProfileEventValue.builder()
					.nonce(resultProfile.getNonce())
					.build());

			return resultProfile;
		} catch (MipException e) {
			log.error("Issue profile failed, e={}", e.getMessage());
			stepEvent.end(e.getError());
			throw e;
		} catch (Exception e) {
			log.error("Issue profile failed, e={}", e.getMessage());
			stepEvent.end(MipError.UNKNOWN_ERROR);
			throw new MipException(MipError.UNKNOWN_ERROR, "Unknown Error");
		} finally {
			stepEventPublisher.publish(stepEvent);
		}
	}

	private ResultProfile createProfile(ServiceInfo serviceInfo) {
		PresentType presentType = serviceInfo.getPresentType();
		if (presentType == PresentType.DID_VP) {
			return createDidVpProfile(serviceInfo);
		}

		if (presentType == PresentType.ZKP_VP) {
			// TODO 영지식 VP 기능 개발 필요
		}
		throw new MipException(MipError.SP_PROFILE_CREATE_ERROR);
	}

	private ResultProfile createDidVpProfile(ServiceInfo serviceInfo) {
		try {
			return didService.getProfile(serviceInfo);
		} catch (IWException | BlockChainException | HttpException e) {
			log.error("create profile failed, e={}", e.getMessage());
			throw new MipException(MipError.SP_PROFILE_CREATE_ERROR, "Create profile failed", e);
		} catch (Exception e) {
			log.error("create profile failed, e={}", e.getMessage());
			throw new MipException(MipError.SP_PROFILE_CREATE_ERROR, "Create profile failed", e);
		}
	}

	@Override
	public void verifyVp(MipMessage message) {
		VpSubmitMessage vpMessage = (VpSubmitMessage) message;
		log.debug("Verify VP submit = {}", vpMessage);

		TransactionInfo transaction = getTransactionInfo(message.getTrxcode());
		if (transaction.getStep() != Step.ISSUE_PROFILE
				|| ErrorInterface.isError(transaction.getResultCode())) {
			throw new MipException(MipError.SP_MSG_SEQ_ERROR,
					String.format("%s(%s)", transaction.getStep(), transaction.getResultCode()));
		}

		validateVp(transaction, vpMessage.getVp());
	}

	private void validateVp(TransactionInfo transaction, Vp vp) {
		DefaultStepEvent stepEvent = DefaultStepEvent.start(transaction.getTransactionCode(), Step.VP_VERIFY);
		try {
			ServiceInfo serviceInfo = getServiceInfo(transaction.getServiceCode());
			validateAuthType(serviceInfo.getAuthTypes(), vp.getAuthType());
			validateNonce(vp, transaction.getNonce());

			// VP 검증
			VpVerifyResult vpVerifyResult = didService.verify(transaction.getServiceCode(), vp);
			if (!vpVerifyResult.isValid()) {
				throw new MipException(MipError.SP_UNSUPPORTED_VP_TYPE, vpVerifyResult.getMessage());
			}

			// VP 저장
			String vpArchiveInfo = vpService.saveVp(transaction.getTransactionCode(), vp);

			VpData vpData = vpService.parseVpData(vp);
			stepEvent.end(MipError.OK,
					StepVerifyEventValue.builder()
							.privacy(vpData.getPrivacy())
							.vp(vp)
							.vpArchiveInfo(vpArchiveInfo)
							.build());

		} catch (MipException e) {
			stepEvent.end(e.getError());
			throw e;
		} catch (CommonException e) {
			stepEvent.end(e.getError());
			throw e;
		} catch (Exception e) {
			log.error("Verify unknown error={}", e.getMessage(), e);
			stepEvent.end(MipError.UNKNOWN_ERROR);
			throw new MipException(MipError.UNKNOWN_ERROR, "Verify failed", e);
		} finally {
			stepEventPublisher.publish(stepEvent);
		}
	}

	/**
	 * authType validation
	 *
	 * 일반인증: profile 의 authType=null
	 * 안심인증: profile 의 authType='["pin", "face"]'
	 * pin, bio, face는 각각 대소문자 구별하지 않음
	 *  1) 일반인증으로 profile을 내린 경우 VP 검증 시 M400.vp.authType 에 "pin" or "bio"를 포함하는지 확인
	 *  2) 안심인증으로 profile을 내린 경우 VP 검증 시 M400.vp.authType 에 ["pin", "face"] 두 가지 모두를 포함하는지 확인
	 */
	private void validateAuthType(List<AuthType> serviceAuthTypeList,
								  List<String> vpAuthTypeList) {
		if (serviceAuthTypeList == null) {
			basicAuthenticate(vpAuthTypeList);
		} else {
			safetyAuthenticate(vpAuthTypeList);
		}
	}

	private void basicAuthenticate(List<String> authTypeList) {
		for (String typeCode : authTypeList) {
			AuthType authType = AuthType.fromCode(typeCode);
			if (authType == AuthType.PIN || authType == AuthType.BIO) {
				return;
			}
		}
		throw new MipException(MipError.SP_MISMATCHING_AUTH_TYPE, "vp.authType");
	}

	private void safetyAuthenticate(List<String> authTypeList) {
		boolean authByPin = false;
		boolean authByFace = false;
		for (String typeCode : authTypeList) {
			AuthType authType = AuthType.fromCode(typeCode);
			if (authType == AuthType.PIN) {
				authByPin = true;
			} else if (authType == AuthType.FACE) {
				authByFace = true;
			}
		}
		if (!authByPin || !authByFace) {
			throw new MipException(MipError.SP_MISMATCHING_AUTH_TYPE, "vp.authType");
		}
	}

	private TransactionInfo getTransactionInfo(String trxcode) {
		return mipServiceHelper.getTransactionInfo(trxcode)
				.orElseThrow(() -> new MipException(MipError.SP_TRXCODE_NOT_FOUND, "trxcode"));
	}

	private ServiceInfo getServiceInfo(String serviceCode) {
		return mipServiceHelper.getServiceInfo(serviceCode)
				.orElseThrow(() -> new MipException(MipError.SP_INVALID_DATA, "svcCode"));
	}

	private void validateNonce(Vp vp, String nonce) {
		if (PresentType.ZKP_VP == vp.getPresentTypeCode()
				&& !vp.getZkpNonce().equalsIgnoreCase(nonce)) {
			throw new MipException(MipError.SP_MISMATCHING_NONCE, "mismatching zkpNonce");
		}
		if (PresentType.DID_VP == vp.getPresentTypeCode()
				&& !vp.getNonce().equalsIgnoreCase(nonce)) {
			throw new MipException(MipError.SP_MISMATCHING_NONCE, "mismatching nonce");
		}
	}

	@Override
	public byte[] getImage(String trxcode, ImageType imageType, String format) throws IOException {
		return imageService.getImage(trxcode, imageType, format);
	}

	@Override
	public ResponseData getTransactionStatus(String trxcode) {
		if (trxcode.equalsIgnoreCase("sample")) {
			return ResponseTransactionData.builder()
					.trxcode("sample")
					.stepCode(Step.VP_VERIFY.name())
					.resultCode("0")
					.build();
		}

		TransactionInfo transaction = mipServiceHelper.getTransactionInfo(trxcode)
				.orElseThrow(() -> new CommonException(MipBizError.TRANSACTION_NOTFOUND, "Not found",
						new NotFoundDetail("transaction", "trxcode", trxcode)));

		return ResponseTransactionData.create(transaction);
	}

	@Override
	public ResponseData getPrivacy(String trxcode) throws IOException {
		if (trxcode.equalsIgnoreCase("sample")) {
			return ResponsePrivacyData.create(vpService.getPrivacy(trxcode, null));
		}
		DefaultStepEvent stepEvent = DefaultStepEvent.start(trxcode, Step.COMPLETE);

		TransactionInfo transaction = getTransactionInfo(trxcode);
		if ((transaction.getStep() != Step.VP_VERIFY && transaction.getStep() != Step.COMPLETE)
				|| ErrorInterface.isError(transaction.getResultCode())) {
			throw new CommonException(MipBizError.INVALID_STEP,
					String.format("%s(%s)", transaction.getStep(), transaction.getResultCode()));
		}

		if (transaction.getStep() == Step.VP_VERIFY) {
			stepEventPublisher.publish(stepEvent.end(MipError.OK));
		}
		try {
			return ResponsePrivacyData.create(vpService.getPrivacy(trxcode, transaction.getVpArchiveInfo()));
		} catch (RuntimeException e) {
			throw new CommonException(MipBizError.VP_ERROR, e);
		}
	}

	@Override
	public void handleError(MipMessage message) {
		log.debug("Received error message, {}", message);
		ErrorMessage errorMessage = (ErrorMessage) message;

		TransactionInfo transaction = getTransactionInfo(message.getTrxcode());
		if (ErrorInterface.isError(transaction.getResultCode())) {
			// 거래가 이미 실패 상태인 경우 이후 발생되는 에러 메시지는 무시한다
			log.warn("This transaction already failed, trx={}, message={}", transaction, message);
			return;
		}

		DefaultStepEvent stepEvent = DefaultStepEvent.start(message.getTrxcode(), transaction.getStep());
		stepEventPublisher.publish(stepEvent.end(getMobileAppError(errorMessage.getErrcode(), errorMessage.getErrmsg())));
	}

	private ErrorInterface getMobileAppError(int errorCode, String message) {
		MipError mipError = MipError.fromCode(errorCode);
		if (mipError != MipError.UNKNOWN_ERROR) {
			return mipError;
		}
		return new MipUnknownError(errorCode, message);
	}
}
