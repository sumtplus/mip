package com.inzisoft.mobileid.sp.domain.transaction.service;

import com.inzisoft.mobileid.sdk.mip.dto.TransactionInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionUpdateParam;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionData;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionFindParam;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionHistoryData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface MipTransactionService {
	Optional<TransactionData> get(String transactionCode);
	@Transactional
	TransactionData save(TransactionInfo transactionInfo);
	@Transactional
	void update(String transactionCode, TransactionUpdateParam updateParam);
	Page<TransactionHistoryData> find(TransactionFindParam param, Pageable pageable);

	void downloadExcelTransaction(TransactionFindParam param, Pageable pageable, HttpServletRequest request, HttpServletResponse response);

}
