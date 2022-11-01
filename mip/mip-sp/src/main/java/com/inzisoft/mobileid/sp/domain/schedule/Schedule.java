package com.inzisoft.mobileid.sp.domain.schedule;

import com.inzisoft.mobileid.config.ScheduleConfig;
import com.inzisoft.mobileid.sp.domain.step.repository.StepResultRepository;
import com.inzisoft.mobileid.sp.domain.summary.repository.SummaryRepository;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionHistoryRepository;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@PropertySource( value = {"classpath:config/schedule.properties"})
public class Schedule {
    @Autowired
    private MipTransactionHistoryRepository mipTransactionHistoryRepository;
    @Autowired
    private MipTransactionRepository mipTransactionRepository;
    @Autowired
    private SummaryRepository summaryRepository;
    @Autowired
    private StepResultRepository stepResultRepository;
    @Autowired
    private ScheduleConfig scheduleConfig;

    @Value("${schedule.summary.deleteOldHistory.term}")
    private long dayBefore;

    @Scheduled(cron = "${schedule.summary.insertHistory.cron}")
    public void scheduleInsertHistoryTask(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = today.format(dateTimeFormatter) + "000000";
        //히스토리 옮기기
        log.info("insertIntoHistoryFromTrx");
        mipTransactionHistoryRepository.insertIntoHistoryFromTrx(date);
        log.info("mipTransaction deleteByDateInfosCreateDateTime");
        mipTransactionRepository.deleteByDateInfosCreateDateTime(date);
    }

    @Scheduled(cron = "${schedule.summary.insertSummary.cron}")
    public void scheduleInsertSummaryTask(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = today.format(dateTimeFormatter) + "000000";
        //통계추가
        log.info("inserIntoSurmmaryFromHistory");
        summaryRepository.inserIntoSurmmaryFromHistory(date);
    }

    @Scheduled(cron = "${schedule.summary.deleteOldHistory.cron}")
    public void scheduleDeleteOldHistoryTask(){
        LocalDate beforeDay = LocalDate.now().minusDays(scheduleConfig.getDeleteOldHistoryTerm()-1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String beforeDate = beforeDay.format(dateTimeFormatter)+ "000000";
        //오래된 히스토리 삭제
        mipTransactionHistoryRepository.deleteByDateInfosCreateDateTime(beforeDate);
    }

    @Scheduled(cron = "${schedule.summary.deleteOldStep.cron}")
    public void scheduleDeleteOldStepTask(){
        LocalDateTime beforeDay = LocalDateTime.of(LocalDate.now().minusDays(scheduleConfig.getDeleteOldStepTerm()), LocalTime.of(23,59,59));
        //오래된 스탭 삭제
        stepResultRepository.deleteByParam(beforeDay);
    }

    @Scheduled(cron = "${schedule.summary.deleteOldSummary.cron}")
    public void scheduleDeleteOldSummaryTask(){
        LocalDate beforeDay = LocalDate.now().minusDays(scheduleConfig.getDeleteOldSummaryTerm());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String beforeDate = beforeDay.format(dateTimeFormatter);
        //오래된 서머리 삭제
        summaryRepository.deleteByParam(beforeDate);
    }
}
