package com.inzisoft.mobileid.sp.domain.schedule;

import com.inzisoft.mobileid.config.ScheduleConfig;
import com.inzisoft.mobileid.provider.jbbank.utils.StringUtil;
import com.inzisoft.mobileid.sp.domain.code.repository.CodeRepository;
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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@PropertySource( value = {"classpath:config/schedule.properties"})
//jhmin 0708 sdk.properties참조 추가
@PropertySource( value = {"classpath:config/sdk.properties"})
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
    //jhmin 0708 코드repository, vp저장소 추가
    @Autowired
    private CodeRepository codeRepository;
    @Value("${vp.localstorage}")
    private String storagePath;
    
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
        
     // jhmin 0617 개발계에서 거래정보 삭제 안하려면 아래 부분 주석처리
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
    
    //jhmin 0708 지난 vp파일 삭제
    @Scheduled(cron = "${schedule.summary.deleteVpFile.cron}")
    public void scheduleDeleteVpFile() {
    	if(!isSchedulingServer()) {
    		log.debug("not server....");
    		return;
    	}
    	LocalDate deleteDate = LocalDate.now().minusDays(1);
    	log.debug("deleteDate : " + deleteDate);
    	
    	String vpsavePath = storagePath+"/"+StringUtil.getStringWithoutHyphen(deleteDate);
    	
    	log.debug(vpsavePath);
    	deleteFolder(vpsavePath);
    }
    
    //jhmin 0708 파일삭제
    public void deleteFolder(String path) {
    	File folder = new File(path);
    	try {
    		if(folder.exists()) {
    			File[] folder_list = folder.listFiles();
    			
    			for(int i = 0; i < folder_list.length; i++) {
    				if(folder_list[i].isFile()) {
    					folder_list[i].delete();
    					log.info(folder_list[i].getName()+" file is deleted");
    				}else {
    					deleteFolder(folder_list[i].getPath()); //재귀함수호출
    				}
    				folder_list[i].delete();
    			}
    			folder.delete();
    		}
    	}catch(Exception e) {
    		e.getStackTrace();
    	}
    }
    
    //jhmin 0708 서버호스트확인
    public boolean isSchedulingServer() {
    	String scheduleHostName = codeRepository.findAllByIdGroupCode("SCHEDULE_HOST").get(0).getId().getCode();
    	log.debug("scheduleHostName : " + scheduleHostName);
    	String hostName = null;
    	try {
    		hostName = InetAddress.getLocalHost().getHostName();
    		log.debug("ServerHostName : " + hostName);
    		if(hostName.equals(scheduleHostName)) {
    			return true;
    		}
    	}catch(UnknownHostException e) {
    		e.printStackTrace();
    	}
    	return false;
    }
}
