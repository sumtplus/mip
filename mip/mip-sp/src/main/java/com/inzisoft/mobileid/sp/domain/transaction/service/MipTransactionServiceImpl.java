package com.inzisoft.mobileid.sp.domain.transaction.service;

import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.sdk.code.error.MipError;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionUpdateParam;
import com.inzisoft.mobileid.sdk.mip.exception.MipException;
import com.inzisoft.mobileid.sp.common.util.ExcelService;
import com.inzisoft.mobileid.sp.domain.service.repository.MipServiceInfo;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionData;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionFindParam;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionHistoryData;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionMoreInfo;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransaction;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionHistory;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionHistoryRepository;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MipTransactionServiceImpl implements MipTransactionService {
	private final ExcelService excelService;
	private final MipTransactionRepository mipTransactionRepository;
	private final MipTransactionHistoryRepository mipTransactionHistoryRepository;

	public MipTransactionServiceImpl(MipTransactionRepository mipTransactionRepository, MipTransactionHistoryRepository mipTransactionHistoryRepository,
									 ExcelService excelService) {
		this.mipTransactionRepository = mipTransactionRepository;
		this.mipTransactionHistoryRepository = mipTransactionHistoryRepository;
		this.excelService = excelService;
	}

	@Override
	public Optional<TransactionData> get(String transactionCode) {
		Optional<MipTransaction> optional = mipTransactionRepository.findByCode(transactionCode);
		if (!optional.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(TransactionData.from(optional.get()));
	}

	@Override
	@Transactional
	public TransactionData save(TransactionInfo transactionInfo) {
		Optional<MipTransaction> optional = mipTransactionRepository.findByCode(transactionInfo.getTransactionCode());
		if (optional.isPresent()) {
			throw new MipException(MipError.UNKNOWN_ERROR, "trxcode already existed");
		}
		return TransactionData.from(mipTransactionRepository.save(createTxVo(transactionInfo)));
	}

	private MipTransaction createTxVo(TransactionInfo transactionInfo) {
		TransactionMoreInfo moreInfo = (TransactionMoreInfo) transactionInfo;
		return MipTransaction.builder()
				.code(transactionInfo.getTransactionCode())
				.mipServiceInfo(MipServiceInfo.builder()
						.code(transactionInfo.getServiceCode())
						.build())
				.interfaceType(transactionInfo.getInterfaceType())
				.submitMode(transactionInfo.getSubmitMode())
				.channelCode(moreInfo.getChannelCode())
				.idType(((TransactionMoreInfo) transactionInfo).getIdType())
				.step(transactionInfo.getStep())
				.resultCode(transactionInfo.getResultCode())
				.deviceId(moreInfo.getDeviceId())
				.branchCode(moreInfo.getBranchCode())
				.branchName(moreInfo.getBranchName())
				.employeeNumber(moreInfo.getEmployeeNumber())
				.employeeName(moreInfo.getEmployeeName())
				.vpArchiveInfo(transactionInfo.getVpArchiveInfo())
				.idArchiveInfo(transactionInfo.getIdArchiveInfo())
				.metadata(moreInfo.getMetadata())
				.build();
	}

	@Override
	@Transactional
	public void update(String transactionCode, TransactionUpdateParam updateParam) {
		MipTransaction transaction = mipTransactionRepository.findByCode(transactionCode)
				.orElseThrow(() -> new MipException(MipError.UNKNOWN_ERROR, "trxcode not found"));

		if (CommonUtil.isNotNullAndEmpty(updateParam.getNonce())) {
			transaction.setNonce(updateParam.getNonce());
		}
		if (updateParam.getStep() != null) {
			transaction.setStep(updateParam.getStep());
		}
		if (updateParam.getIdType() != null) {
			transaction.setIdType(updateParam.getIdType());
		}
		if (updateParam.getResultCode() != null) {
			transaction.setResultCode(updateParam.getResultCode());
		}
		if (CommonUtil.isNotNullAndEmpty(updateParam.getCustomerBirth())) {
			transaction.setCustomerBirth(updateParam.getCustomerBirth());
		}
		if (CommonUtil.isNotNullAndEmpty(updateParam.getCustomerName())) {
			transaction.setCustomerName(updateParam.getCustomerName());
		}
		if (CommonUtil.isNotNullAndEmpty(updateParam.getVpArchiveInfo())) {
			transaction.setVpArchiveInfo(updateParam.getVpArchiveInfo());
		}
		if (CommonUtil.isNotNullAndEmpty(updateParam.getIdArchiveInfo())) {
			transaction.setIdArchiveInfo(updateParam.getIdArchiveInfo());
		}
	}

	@Override
	public Page<TransactionHistoryData> find(TransactionFindParam param, Pageable pageable) {
		// TODO HISTORY 테이블에서 같이 조회하도록 구현 필요
		Page<MipTransactionHistory> transactionPage = mipTransactionHistoryRepository.findAllByParams(param, pageable);
		return new PageImpl<>(TransactionHistoryData.from(transactionPage.getContent()), pageable, transactionPage.getTotalElements());
	}

	@Override
	public void downloadExcelTransaction(TransactionFindParam param, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		String fileName = "Mip Transaction List";
		Date date = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String title = "TransactionList_"+dateFormat.format(date);
		String[] headers = {"거래코드",
				"서비스코드",
				"인터페이스 타입",
				"제출 방식",
				"채널 코드",
				"검증 nonce",
				"거래 단계 코드",
				"결과 코드",
				"신분증 타입",
				"등록일시",
				"수정일시",
				"지점코드",
				"지점명",
				"직원번호",
				"직원명",
				"응대장치 아이디",
				"고객명",
				"고객 생년월일",
				"VP 저장 정보",
				"신분증 이미지 저장 정보",
				"거래 요청 메타 데이터"
		};
		String[] bodyFields ={
				"transactionCode",
				"serviceCode",
				"interfaceType",
				"submitMode",
				"channelCode",
				"nonce",
				"stepCode",
				"resultCode",
				"idType",
				"createdAt",
				"updatedAt",
				"branchCode",
				"branchName",
				"employeeNo",
				"employeeName",
				"deviceId",
				"customerName",
				"customerBirth",
				"vpArchiveInfo",
				"idArchiveInfo",
				"metadata"
		};
		try {
			Page<MipTransactionHistory> transactionPage = mipTransactionHistoryRepository.findAllByParams(param, pageable);
			List<TransactionHistoryData> list = TransactionHistoryData.from(transactionPage.getContent());
			List<Object> datas = new ArrayList<Object>();
			for(int i=0; i<list.size(); i++) {
				datas.add((Object)list.get(i));
			}
			excelService.downloadExcelFile(response, fileName, title, headers, bodyFields, datas);

		} catch(Exception e) {
			log.error("downloadExeclResultbyContract error: {}", e.getMessage());
		}
	}


}
