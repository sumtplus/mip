package com.inzisoft.mobileid.provider.jbbank.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.inzisoft.mobileid.provider.jbbank.dto.RequestMDLSend;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransaction;

/**
 * 시퀀스를 가져오기 위한 repository 
 * @author jhmin
 *
 */
public interface CommonRepository extends Repository<MipTransaction, String>{
	
	// 시퀀스 출력
	@Query(value = "SELECT SQ_MIP_TRXCODE_SERIAL.NEXTVAL FROM DUAL", nativeQuery = true)
	Long selectTrxcodeSequence();
}
