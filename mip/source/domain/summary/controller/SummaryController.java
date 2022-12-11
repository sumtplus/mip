package com.inzisoft.mobileid.sp.domain.summary.controller;

import com.inzisoft.mobileid.sp.domain.summary.dto.SummaryFindParam;
import com.inzisoft.mobileid.sp.domain.summary.service.SummaryService;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionFindParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1/api/summary")
public class SummaryController {

    private SummaryService summaryService;

    public SummaryController(SummaryService summaryService){
        this.summaryService = summaryService;
    }

    @GetMapping(value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity find(@ModelAttribute SummaryFindParam param,
                               @PageableDefault(size = 10)
                               @SortDefault.SortDefaults({
                                       @SortDefault(sort = "trxDate", direction = Sort.Direction.DESC),
                                       @SortDefault(sort = "txOpenCount", direction = Sort.Direction.DESC),
                                       @SortDefault(sort = "vpVerifyCount", direction = Sort.Direction.DESC),
                                       @SortDefault(sort = "completeCount", direction = Sort.Direction.DESC)
                               })
                                       Pageable pageable){
        return ResponseEntity.ok(summaryService.find(param, pageable));
    }

    @GetMapping(value = "/count/total",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTotalCount(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
        return ResponseEntity.ok(summaryService.getTotalCount(startDate, endDate));
    }

    @GetMapping(value = "/count/topbranch",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTopBranchCount(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
        return ResponseEntity.ok(summaryService.getTopBranchCount(startDate, endDate));
    }

    @GetMapping(value = "/count/columns",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getSummaryTotalCount(@ModelAttribute SummaryFindParam param, @RequestParam("endDate") String endDate){
        return ResponseEntity.ok(summaryService.getSummaryTotalCount(param));
    }

    @GetMapping(value = "/download")
    public void getExcelDownload(@ModelAttribute SummaryFindParam param, @PageableDefault(size = Integer.MAX_VALUE, sort = "trxDate", direction = Sort.Direction.DESC) Pageable pageable,
                                 HttpServletRequest request, HttpServletResponse response){
        summaryService.downloadExcelSummary(param, pageable,request, response);
    }

}
