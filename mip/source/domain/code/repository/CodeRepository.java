package com.inzisoft.mobileid.sp.domain.code.repository;

import com.inzisoft.mobileid.sp.domain.code.dto.CodeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CodeRepository extends JpaRepository<MipCode, MipCodeId> {
	@Query(value = "select group_code " +
			"from mip_code " +
			"group by group_code",
			nativeQuery = true)
	List<String> findAllGroupCode();

	List<MipCode> findAllByIdGroupCode(String groupCode);
}
