package com.inzisoft.mobileid.sp.domain.summary.service;

import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryBranchDataProjection;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryCountData;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryData;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryFindParam;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionFindParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface SummaryService {

    Page<SummaryData> find(SummaryFindParam param, Pageable pageable);

    int getTotalCount(String startDate, String endDate);
    List<SummaryBranchDataProjection> getTopBranchCount(String startDate, String endDate);

    SummaryCountData getSummaryTotalCount(SummaryFindParam param);

    void downloadExcelSummary(SummaryFindParam param, Pageable pageable, HttpServletRequest request, HttpServletResponse response);

}
