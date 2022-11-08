package com.inzisoft.mobileid.sp.domain.transaction.event;

import com.inzisoft.mobileid.common.exception.ErrorInterface;
import com.inzisoft.mobileid.common.exception.MipBizError;
import com.inzisoft.mobileid.sdk.code.DrivingLicenseVc;
import com.inzisoft.mobileid.sdk.code.Step;
import com.inzisoft.mobileid.sdk.event.step.StepEvent;
import com.inzisoft.mobileid.sdk.event.step.StepEventHandler;
import com.inzisoft.mobileid.sdk.event.step.value.StepOpenEventValue;
import com.inzisoft.mobileid.sdk.event.step.value.StepProfileEventValue;
import com.inzisoft.mobileid.sdk.event.step.value.StepVerifyEventValue;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionUpdateParam;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;

import javax.transaction.Transactional;
import java.util.EnumSet;

@Slf4j
public class TransactionUpdateHandler implements StepEventHandler {
	private EnumSet<Step> eventTarget = EnumSet.allOf(Step.class);
	private final MipTransactionService mipTransactionService;

	public TransactionUpdateHandler(MipTransactionService mipTransactionService) {
		this.mipTransactionService = mipTransactionService;
	}

	@Override
	public EnumSet<Step> getSteps() {
		return eventTarget;
	}

	@Override
	public boolean ignoreException() {
		return true;
	}

	@Override
	@Transactional
	public void onEvent(StepEvent stepEvent) throws MipException {
		log.debug("onEvent {}", stepEvent);
		if (stepEvent.getStep() == Step.OPEN_TRANSACTION
				&& stepEvent.getEventValue() instanceof StepOpenEventValue) {
			onOpenTransaction(stepEvent);
		}
		if (stepEvent.getStep() == Step.ISSUE_PROFILE) {
			onIssueProfile(stepEvent);
		}
		if (stepEvent.getStep() == Step.VP_VERIFY) {
			onVerify(stepEvent);
		}
		if (stepEvent.getStep() == Step.COMPLETE) {
			onComplete(stepEvent);
		}
	}

	private void onOpenTransaction(StepEvent stepEvent) {
		try {
			StepOpenEventValue stepOpenEventValue = (StepOpenEventValue) stepEvent.getEventValue();
			mipTransactionService.save(stepOpenEventValue.getTransactionInfo());
		} catch (DataAccessException e) {
			throw new MipException(MipBizError.TRANSACTION_UPDATE_ERROR,
					String.format("Update transaction %s failed, openTransaction", stepEvent.getTrxcode()), e);
		}
	}

	private void onIssueProfile(StepEvent stepEvent) {
		try {
			mipTransactionService.update(stepEvent.getTrxcode(), getUpdateParam(stepEvent));
		} catch (DataAccessException e) {
			throw new MipException(MipBizError.TRANSACTION_UPDATE_ERROR,
					String.format("Update transaction %s failed, issueProfile", stepEvent.getTrxcode()), e);
		}
	}

	private void onVerify(StepEvent stepEvent) {
		try {
			mipTransactionService.update(stepEvent.getTrxcode(), getUpdateParam(stepEvent));
		} catch (DataAccessException e) {
			throw new MipException(MipBizError.TRANSACTION_UPDATE_ERROR,
					String.format("Update transaction %s failed, verify", stepEvent.getTrxcode()), e);
		}
	}

	private TransactionUpdateParam getUpdateParam(StepEvent stepEvent) {
		if (ErrorInterface.isSuccess(stepEvent.getResultCode())) {
			if (Step.OPEN_TRANSACTION == stepEvent.getStep()) {
			}
			if (Step.ISSUE_PROFILE == stepEvent.getStep()) {
				return getUpdateParamWithIssueProfile(stepEvent);
			}
			if (Step.VP_VERIFY == stepEvent.getStep()) {
				return getUpdateParamWithVpVerify(stepEvent);
			}
			if (Step.COMPLETE == stepEvent.getStep()) {
			}
		}
		return TransactionUpdateParam.builder()
				.step(stepEvent.getStep())
				.resultCode(stepEvent.getResultCode())
				.build();
	}

	private TransactionUpdateParam getUpdateParamWithIssueProfile(StepEvent stepEvent) {
		StepProfileEventValue stepValue = (StepProfileEventValue) stepEvent.getEventValue();
		return TransactionUpdateParam.builder()
				.step(stepEvent.getStep())
				.resultCode(stepEvent.getResultCode())
				.nonce(stepValue.getNonce())
				.build();
	}
	private TransactionUpdateParam getUpdateParamWithVpVerify(StepEvent stepEvent) {
		StepVerifyEventValue value = (StepVerifyEventValue) stepEvent.getEventValue();
		return TransactionUpdateParam.builder()
				.step(stepEvent.getStep())
				.resultCode(stepEvent.getResultCode())
				.idType(value.getIdType())
				.customerName(value.getPrivacy().get(DrivingLicenseVc.NAME.getCode()))
				.customerBirth(value.getPrivacy().get(DrivingLicenseVc.BIRTH.getCode()))
				.vpArchiveInfo(value.getVpArchiveInfo())
				.build();
	}

	private void onComplete(StepEvent stepEvent) {
		try {
			mipTransactionService.update(stepEvent.getTrxcode(), getUpdateParam(stepEvent));
		} catch (DataAccessException e) {
			throw new MipException(MipBizError.TRANSACTION_UPDATE_ERROR,
					String.format("Update transaction %s failed, complete", stepEvent.getTrxcode()), e);
		}
	}

	@Override
	public void onError(StepEvent stepEvent) throws MipException {
		log.error("onError {}", stepEvent);
		try {
			TransactionUpdateParam param = TransactionUpdateParam.builder()
					.step(stepEvent.getStep())
					.resultCode(stepEvent.getResultCode())
					.build();
			mipTransactionService.update(stepEvent.getTrxcode(), param);
		} catch (DataAccessException e) {
			throw new MipException(MipBizError.TRANSACTION_UPDATE_ERROR,
					String.format("Update transaction %s failed, error", stepEvent.getTrxcode()), e);
		}
	}
}
