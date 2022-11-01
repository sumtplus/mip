package com.inzisoft.mobileid.sp.domain.transaction.repository;

import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionFindParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MipTransactionRepository extends JpaRepository<MipTransaction, String> {
	Optional<MipTransaction> findByCode(String transactionCode);

	// TODO CountQuery 추가 필요
	@Query(value = "SELECT ti FROM MipTransaction ti" +
			"  WHERE ti.dateInfos.createDateTime BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate}" +
			" AND (ti.idType is null OR :#{#findParam.idType} = ti.idType)" +
			" AND (:#{#findParam.branchName} is null OR ti.branchName LIKE %:#{#findParam.branchName}%) " +
			" AND (:#{#findParam.employeeNumber} is null OR ti.employeeNumber LIKE :#{#findParam.employeeNumber}%) "+
			" AND (:#{#findParam.customerName} is null OR ti.customerName LIKE :#{#findParam.customerName}%) "+
			" AND (:#{#findParam.channelCode} is null OR ti.channelCode LIKE :#{#findParam.channelCode}%) "
			// CHECK DATE

	)
	Page<MipTransaction> findAllByParams(@Param("findParam") TransactionFindParam findParam, Pageable pageable);

	@Transactional
	@Modifying
	@Query(value = "delete from mip_transaction where create_dttm < :date", nativeQuery = true)
	void deleteByDateInfosCreateDateTime(@Param("date") String date);

}
