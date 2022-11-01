package com.inzisoft.mobileid.sp.domain.admin.repository;

import com.inzisoft.mobileid.sp.domain.admin.dto.AdminFindParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin, String> {
	
	// TODO CountQuery 추가 필요
	@Query(value = "SELECT adm FROM Admin adm" +
			" WHERE (:#{#findParam.employeeName} is null OR adm.employeeName LIKE %:#{#findParam.employeeName}%)" +
			" AND (:#{#findParam.employeeNumber} is null OR adm.employeeNumber LIKE :#{#findParam.employeeNumber}%)" +
			" AND (adm.deleted = :#{#findParam.deleted})")
	Page<Admin> findAllByParams(@Param("findParam") AdminFindParam param, Pageable pageable);

	int countByAuthCodeAndDeletedFalse(String authCode);

}
