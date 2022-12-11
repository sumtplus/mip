package com.inzisoft.mobileid.sp.domain.summary.repository;

import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryBranchDataProjection;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryCountData;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryFindParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface SummaryRepository extends JpaRepository<Summary, String> {

    // TODO CountQuery 추가 필요
    @Query(value =  "SELECT smry FROM Summary smry " +
             "  WHERE smry.trxDate BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate} "+
            " AND (:#{#findParam.idType} is null OR smry.idType = :#{#findParam.idType}) " +
            " AND (:#{#findParam.branchName} is null OR smry.branchName LIKE %:#{#findParam.branchName}%) " +
             " AND (:#{#findParam.employeeNumber} is null OR smry.employeeNumber LIKE :#{#findParam.employeeNumber}%) "
            // CHECK DATE
             )
    Page<Summary> findAllByParams(@Param( "findParam") SummaryFindParam findParam, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value =
             "INSERT INTO mip_emp_daily_smry (ID, EMPLOYEE_NO, TRX_DATE, ID_TYPE, BRANCH_CODE, BRANCH_NAME, EMPLOYEE_NAME, TX_OPEN_COUNT, VP_VERIFY_COUNT, COMPLETE_COUNT)  "+
                     "SELECT SQ_MIP_EMP_DAILY_SMRY_ID.NEXTVAL AS ID,  TB1.EMPLOYEE_NO AS EMPLOYEE_NO,  TB1.TRX_DATE AS TRX_DATE,  TB1.ID_TYPE AS ID_TYPE,  TB1.BRANCH_CODE AS BRANCH_CODE,  TB1.BRANCH_NAME AS BRANCH_NAME,  TB1.EMPLOYEE_NAME AS EMPLOYEE_NAME,  TB1.TX_OPEN_COUNT AS TX_OPEN_COUNT,  TB1.VP_VERIFY_COUNT AS VP_VERIFY_COUNT,  TB1.COMPLETE_COUNT AS COMPLETE_COUNT " +
                     "FROM ( "+
                         "SELECT  EMPLOYEE_NO, substr(CREATE_DTTM, 1, 8) AS TRX_DATE, ID_TYPE, BRANCH_CODE, BRANCH_NAME, EMPLOYEE_NAME,  " +
                         "SUM(CASE  " +
                                 "WHEN STEP_CODE = 'OPEN_TRANSACTION' AND RESULT_CODE = '0' THEN 1  " +
                                 "WHEN STEP_CODE = 'VP_VERIFY' THEN 1   " +
                                 "WHEN STEP_CODE = 'COMPLETE' THEN 1 ELSE 0   " +
                                 "END "+
                         ") AS TX_OPEN_COUNT,  " +
                         "SUM(CASE  " +
                                 "WHEN STEP_CODE = 'VP_VERIFY' AND RESULT_CODE = '0' THEN 1  " +
                                 "WHEN STEP_CODE = 'COMPLETE' THEN 1 ELSE 0   " +
                                 "END "+
                         ") AS VP_VERIFY_COUNT, " +
                         "SUM(CASE  " +
                                 "WHEN STEP_CODE = 'COMPLETE' AND RESULT_CODE = '0' THEN 1 ELSE 0 END  " +
                         ") AS COMPLETE_COUNT  " +
                         "FROM mip_trx_history   " +
                         "WHERE CREATE_DTTM < :date AND INTERFACE_TYPE = 'QR_MPM' " +
                         "GROUP BY EMPLOYEE_NO, substr(CREATE_DTTM, 1, 8), ID_TYPE, BRANCH_NAME, EMPLOYEE_NAME, BRANCH_CODE)  TB1  ", nativeQuery = true
    )
    void inserIntoSurmmaryFromHistory(@Param( "date") String date);


    //신분증생성건수
    @Query(value = "select NVL(sum(VP_VERIFY_COUNT),0) from mip_emp_daily_smry where trx_date between :startDate and :endDate", nativeQuery = true)
    int getAllCountOfVpVerifyCount(@Param("startDate")String startDate, @Param("endDate")String endDate);

    //신분증 생성 상위 10개 소속부점점
    @Query(value = "SELECT * FROM (SELECT A.*, ROWNUM AS RNUM FROM ( SELECT BRANCH_NAME AS branchName, sum(VP_VERIFY_COUNT) AS totalCount from mip_emp_daily_smry where trx_date between :startDate and :endDate group by BRANCH_NAME order by totalCount DESC ) A WHERE ROWNUM <= 10) WHERE RNUM > 0 ", nativeQuery = true)
    List<SummaryBranchDataProjection> getBranchCountOfVpVerifyList(@Param("startDate")String startDate, @Param("endDate")String endDate);

    //컬럼별 총합
    @Query(value = "select NVL(sum(TX_OPEN_COUNT),0) as totalTxOpenCount, NVL(sum(VP_VERIFY_COUNT),0) as totalVpVerifyCount, NVL(sum(COMPLETE_COUNT),0) as totalCompleteCount " +
            "from mip_emp_daily_smry " +
            "  WHERE trx_date BETWEEN :#{#findParam.startDate} AND :#{#findParam.endDate} "+
            " AND (:#{#findParam.idType} is null OR id_type = :#{#findParam.idType}) " +
            " AND (:#{#findParam.branchName} is null OR branch_name LIKE %:#{#findParam.branchName}%) " +
            " AND (:#{#findParam.employeeNumber} is null OR employee_no LIKE :#{#findParam.employeeNumber}%) "
            , nativeQuery = true)
    SummaryCountData getSummaryTotalCount(@Param( "findParam") SummaryFindParam findParam);


    @Transactional
    @Modifying
    @Query(value = "delete from mip_emp_daily_smry where TRX_DATE <= :date", nativeQuery = true)
    void deleteByParam(@Param( "date") String date);



}
