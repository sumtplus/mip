package com.inzisoft.mobileid.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ToString
@PropertySource( value = {"classpath:config/schedule.properties"})
public class ScheduleConfig {
    @Value("${schedule.summary.insertHistory.cron}")
    private String insertHistoryCron;
    @Value("${schedule.summary.insertSummary.cron}")
    private String insertSummaryCron;
    @Value("${schedule.summary.deleteTransaction.cron}")
    private String deleteTransactionCron;
    @Value("${schedule.summary.deleteOldHistory.cron}")
    private String deleteOldHistoryCron;
    @Value("${schedule.summary.deleteOldStep.cron}")
    private String deleteOldStepCron;
    @Value("${schedule.summary.deleteOldSummary.cron}")
    private String deleteOldSummaryCron;
    @Value("${schedule.summary.deleteOldHistory.term}")
    private int deleteOldHistoryTerm;
    @Value("${schedule.summary.deleteOldStep.term}")
    private int deleteOldStepTerm;
    @Value("${schedule.summary.deleteOldSummary.term}")
    private int deleteOldSummaryTerm;
    // jhmin 0713
    @Value("${schedule.summary.deleteVpFile.cron}")
    private String deleteVpFileCron;
}
