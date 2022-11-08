package com.inzisoft.mobileid.config;

import com.inzisoft.mobileid.provider.jbbank.service.CommonService;
import com.inzisoft.mobileid.provider.jbbank.service.CommonServiceImpl;
import com.inzisoft.mobileid.provider.jbbank.service.FileTestService;
import com.inzisoft.mobileid.provider.jbbank.service.FileTestServiceImpl;
import com.inzisoft.mobileid.sdk.config.SdkConfig;
import com.inzisoft.mobileid.sdk.event.step.StepEventPublisher;
import com.inzisoft.mobileid.sdk.generator.TransactionCodeGenerator;
import com.inzisoft.mobileid.sdk.image.generator.IdCardGenerator;
import com.inzisoft.mobileid.sdk.mip.config.SpConfig;
import com.inzisoft.mobileid.sdk.mip.message.MessageFactory;
import com.inzisoft.mobileid.sdk.mip.raon.RaonSdkHelper;
import com.inzisoft.mobileid.sdk.mip.vp.VpDataParser;
import com.inzisoft.mobileid.sdk.service.*;
import com.inzisoft.mobileid.sdk.service.impl.*;
import com.inzisoft.mobileid.sdk.storage.vp.VpStorageBase;
import com.inzisoft.mobileid.sp.common.util.ExcelService;
import com.inzisoft.mobileid.sp.common.util.ExcelServiceImpl;
import com.inzisoft.mobileid.sp.domain.code.repository.CodeRepository;
import com.inzisoft.mobileid.sp.domain.mip.service.MipServiceHelperImpl;
import com.inzisoft.mobileid.sp.domain.service.repository.MipServiceRepository;
import com.inzisoft.mobileid.sp.domain.service.service.MipServiceInfoService;
import com.inzisoft.mobileid.sp.domain.service.service.MipServiceInfoServiceImpl;
import com.inzisoft.mobileid.sp.domain.summary.repository.SummaryRepository;
import com.inzisoft.mobileid.sp.domain.summary.service.SummaryService;
import com.inzisoft.mobileid.sp.domain.summary.service.SummaryServiceImpl;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionHistoryRepository;
import com.inzisoft.mobileid.sp.domain.transaction.repository.MipTransactionRepository;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionService;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@ComponentScan(basePackages = {"com.inzisoft.mobileid"})
@Configuration
public class BeanConfig {
	@Bean
	public MessageFactory messageFactory() {
		return new MessageFactory();
	}

	@Bean
	public RaonSdkHelper raonSdkHelper(SpConfig spConfig) throws Exception {
		return new RaonSdkHelper(spConfig);
	}

	@Bean
	public DidService didService(RaonSdkHelper raonSdkHelper, VpDataParser vpDataParser) {
		return new RaonSdkDidService(raonSdkHelper, vpDataParser);
	}

	@Bean
	public MessageService messageService() {
		return new MessageServiceImpl();
	}

	@Bean
	public MipServiceHelper mipServiceHelper(MipServiceInfoService mipServiceInfoService,
											 MipTransactionService mipTransactionService,
											 TransactionCodeGenerator transactionCodeGenerator) {
		return new MipServiceHelperImpl(mipServiceInfoService, mipTransactionService, transactionCodeGenerator);
	}

	@Bean
	public MipBizService mipBizService(SpConfig spConfig,
									   SdkConfig sdkConfig,
									   DidService didService,
									   MipServiceHelper mipServiceHelper,
									   MessageService messageService,
									   ImageService imageService,
									   VpService vpService,
									   StepEventPublisher stepEventPublisher) {
		return new MipBizServiceImpl(spConfig,
				sdkConfig,
				didService,
				mipServiceHelper,
				messageService,
				imageService,
				vpService,
				stepEventPublisher);
	}

	@Bean
	public MipServiceInfoService mipServiceInfoService(MipServiceRepository mipServiceRepository) {
		return new MipServiceInfoServiceImpl(mipServiceRepository);
	}

	@Bean
	public MipTransactionService mipTransactionService(MipTransactionRepository mipTransactionRepository, MipTransactionHistoryRepository mipTransactionHistoryRepository, ExcelService excelService) {
		return new MipTransactionServiceImpl(mipTransactionRepository, mipTransactionHistoryRepository, excelService);
	}

	@Bean
	public SummaryService summaryService(SummaryRepository summaryRepository, ExcelService excelService) {
		return new SummaryServiceImpl(summaryRepository, excelService);
	}

	@Bean
	public VpService vpService(VpStorageBase vpStorage, VpDataParser vpDataParser) {
		return new VpServiceImpl(vpStorage, vpDataParser);
	}

	@Bean
	public ExcelService excelService(MipServiceRepository mipServiceRepository, CodeRepository codeRepository){
		return new ExcelServiceImpl(mipServiceRepository, codeRepository);
	}

	@Bean
	public VpDataParser vpDataParser(RaonSdkHelper raonSdkHelper) {
		return new VpDataParser(raonSdkHelper);
	}

	@Bean
	public ImageService imageService(MipServiceHelper mipServiceHelper,
									 SpConfig spConfig,
									 MessageService messageService,
									 VpService vpService,
									 IdCardGenerator idcardGenerator) {
		return new ImageServiceImpl(mipServiceHelper, spConfig, messageService, vpService, idcardGenerator);
	}
	
	//jhmin 0524
	@Bean
	public CommonService commonService(MipServiceHelper mipServiceHelper, 
										MipTransactionService mipTransactionService,
										VpService vpService,
										IdCardGenerator idCardGenerator,
										WebClient webClient) {
		return new CommonServiceImpl(mipServiceHelper, mipTransactionService, vpService, idCardGenerator, webClient);
	}
	
	//jhmin 0527
		@Bean
	public FileTestService fileTestService() {
		return new FileTestServiceImpl();
	}
}
