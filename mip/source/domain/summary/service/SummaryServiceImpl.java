package com.inzisoft.mobileid.sp.domain.summary.service;

import com.inzisoft.mobileid.sp.common.util.ExcelService;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryBranchDataProjection;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryCountData;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryData;
import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryFindParam;
import com.inzisoft.mobileid.sp.domain.summary.repository.Summary;
import com.inzisoft.mobileid.sp.domain.summary.repository.SummaryRepository;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionHistoryData;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
public class SummaryServiceImpl implements SummaryService{

    private final ExcelService excelService;
    private final SummaryRepository summaryRepository;

    public SummaryServiceImpl(
            SummaryRepository summaryRepository, ExcelService excelService) {
        this.summaryRepository = summaryRepository;
        this.excelService = excelService;
    }


    @Override
    public Page<SummaryData> find(SummaryFindParam param, Pageable pageable) {
        Page<Summary> summaryPage = summaryRepository.findAllByParams(param, pageable);
        return new PageImpl<>(SummaryData.from(summaryPage.getContent()),pageable,summaryPage.getTotalElements() );
    }

    @Override
    public int getTotalCount(String startDate, String endDate) {
        return summaryRepository.getAllCountOfVpVerifyCount(startDate, endDate);
    }

    @Override
    public List<SummaryBranchDataProjection> getTopBranchCount(String startDate, String endDate) {
        return summaryRepository.getBranchCountOfVpVerifyList(startDate, endDate);
    }

    @Override
    public SummaryCountData getSummaryTotalCount(SummaryFindParam param) {
        return summaryRepository.getSummaryTotalCount(param);
    }

    @Override
    public void downloadExcelSummary(SummaryFindParam param, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
        String fileName = "Mip Summary List";
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String title = "SummaryList_"+dateFormat.format(date);
        String[] headers = {
				"직원번호",
				"직원명",
				"거래날짜",
				"신분증 타입",
				"지점코드",
				"지점명",
				"거래 요청 건 수",
				"VP 검증 건 수",
				"거래 완료 건 수"
        };
        String[] bodyFields ={
                "employeeNumber",
                "employeeName",
                "trxDate",
                "idType",
                "branchCode",
                "branchName",
                "txOpenCount",
                "vpVerifyCount",
                "completeCount"
        };
        try {
            Page<Summary> summaryPage = summaryRepository.findAllByParams(param, pageable);
            List<SummaryData> list = SummaryData.from(summaryPage.getContent());
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
