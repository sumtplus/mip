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

public interface MipTransactionHistoryRepository extends JpaRepository<MipTransactionHistory, String> {
	Optional<MipTransactionHistory> findByCode(String transactionCode);

	@Query(value =
			"select * from(" +
					"SELECT * FROM mip_transaction  " +
					" WHERE create_dttm BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate} " +
					" AND (:#{#findParam.idType} is null OR id_type = :#{#findParam.idType} ) " +
					" AND (:#{#findParam.branchName} is null OR branch_name LIKE %:#{#findParam.branchName}% )" +
					" AND (:#{#findParam.employeeNumber} is null OR employee_no LIKE :#{#findParam.employeeNumber}%) " +
					" AND (:#{#findParam.customerName} is null OR customer_name LIKE %:#{#findParam.customerName}%) " +
					" AND (:#{#findParam.channelCode} is null OR channel_code LIKE :#{#findParam.channelCode}%) " +
					"UNION " +
					"SELECT * FROM mip_trx_history " +
					"  WHERE create_dttm BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate} " +
					" AND (:#{#findParam.idType}  is null OR id_type = :#{#findParam.idType} ) " +
					" AND (:#{#findParam.branchName} is null OR branch_name LIKE %:#{#findParam.branchName}% )" +
					" AND (:#{#findParam.employeeNumber} is null OR employee_no LIKE :#{#findParam.employeeNumber}% )" +
					" AND (:#{#findParam.customerName} is null OR customer_name LIKE %:#{#findParam.customerName}%) " +
					" AND (:#{#findParam.channelCode} is null OR channel_code LIKE :#{#findParam.channelCode}%) " +
					")"
			, countQuery = "select count(trx_code) from(" +
					"SELECT trx_code FROM mip_transaction  " +
					" WHERE create_dttm BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate} " +
					" AND (:#{#findParam.idType} is null OR id_type = :#{#findParam.idType} ) " +
					" AND (:#{#findParam.branchName} is null OR branch_name LIKE %:#{#findParam.branchName}% )" +
					" AND (:#{#findParam.employeeNumber} is null OR employee_no LIKE :#{#findParam.employeeNumber}%) " +
					" AND (:#{#findParam.customerName} is null OR customer_name LIKE %:#{#findParam.customerName}%) " +
					" AND (:#{#findParam.channelCode} is null OR channel_code LIKE :#{#findParam.channelCode}%) " +
					"UNION " +
					"SELECT trx_code FROM mip_trx_history " +
					"  WHERE create_dttm BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate} " +
					" AND (:#{#findParam.idType}  is null OR id_type = :#{#findParam.idType} ) " +
					" AND (:#{#findParam.branchName} is null OR branch_name LIKE %:#{#findParam.branchName}% )" +
					" AND (:#{#findParam.employeeNumber} is null OR employee_no LIKE :#{#findParam.employeeNumber}% )" +
					" AND (:#{#findParam.customerName} is null OR customer_name LIKE %:#{#findParam.customerName}%) " +
					" AND (:#{#findParam.channelCode} is null OR channel_code LIKE :#{#findParam.channelCode}%) " +
					")"
			, nativeQuery = true)
	Page<MipTransactionHistory> findAllByParams(@Param("findParam") TransactionFindParam findParam,
												Pageable pageable);

	@Transactional
	@Modifying
	@Query(value ="insert into mip_trx_history " +
			"select * from mip_transaction where CREATE_DTTM < :date", nativeQuery = true)
	void insertIntoHistoryFromTrx(@Param("date") String date);

	@Transactional
	@Modifying
	@Query(value = "delete from mip_trx_history where create_dttm < :date", nativeQuery = true)
	void deleteByDateInfosCreateDateTime(@Param("date") String date);
}
