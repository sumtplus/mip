package com.inzisoft.mobileid.provider.jbbank.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.exception.Error;
import com.inzisoft.mobileid.common.exception.cause.NotFoundDetail;
import com.inzisoft.mobileid.provider.jbbank.dto.RequestMDLSend;
import com.inzisoft.mobileid.provider.jbbank.dto.ResponseBizResult;
import com.inzisoft.mobileid.provider.jbbank.utils.Const;
import com.inzisoft.mobileid.sdk.code.DrivingLicenseVc;
import com.inzisoft.mobileid.sdk.code.Step;
import com.inzisoft.mobileid.sdk.image.generator.IdCardGenerator;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionInfo;
import com.inzisoft.mobileid.sdk.mip.dto.TransactionUpdateParam;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseData;
import com.inzisoft.mobileid.sdk.mip.response.data.ResponseTransactionData;
import com.inzisoft.mobileid.sdk.service.MipServiceHelper;
import com.inzisoft.mobileid.sdk.service.VpService;
import com.inzisoft.mobileid.sp.domain.transaction.dto.TransactionData;
import com.inzisoft.mobileid.sp.domain.transaction.service.MipTransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonServiceImpl implements CommonService{
	private MipServiceHelper mipServiceHelper;
	
	private final MipTransactionService mipTransactionService;
	
	private VpService vpService;
	
	private final IdCardGenerator idCardGenerator;
	
	private WebClient webClient;
	
	// 생성자 수정필요?
	public CommonServiceImpl(MipServiceHelper mipServiceHelper, MipTransactionService mipTransactionService,
			VpService vpService, IdCardGenerator idCardGenerator, WebClient webClient) {
		this.mipServiceHelper = mipServiceHelper;
		this.mipTransactionService = mipTransactionService;
		this.vpService = vpService;
		this.idCardGenerator = idCardGenerator;
		this.webClient = webClient;
	}
	
	/**
	 * BPR에 거래정보 및 이미지 전송
	 * @author jhmin
	 * @throws IOException 
	 */
	@Override
	public void sendMdlImage(String transactionCode, String vpArchiveInfo) throws IOException {
		RequestMDLSend requestMDLSend = new RequestMDLSend(); 
		// 거래코드로 거래정보 읽기
		TransactionInfo transaction = getTransaction(transactionCode);
		TransactionData transactionData = mipTransactionService.get(transactionCode).get();
		// 거래상태 확인
//		if (transaction.getStep() != Step.VP_VERIFY && transaction.getStep() != Step.COMPLETE) {
//			throw new CommonException(CommonError.INVALID_STEP, String.format("step=%s", transaction.getStep()));
//		}
		// vp정보 읽기
//		Map<String, String> privacy = vpService.getPrivacy(transactionCode, vpArchiveInfo);
		Map<String, String> privacy = vpService.getPrivacy("sample", null); // 샘플 vp
		// vp정보로 신분증이미지 생성
//		byte[] bytes = idCardGenerator.createIdCard(idCardGenerator.createDriverLicenseData(privacy, Collections.emptyList()));
		File file = new File("C:/Users/User/Desktop/work/MIP_v3.0_20220523/src/mip-sp/src/main/resources/inzisoft/images/dl_sample.jpg");
		byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(file.toPath());
			log.debug("file length : " + String.valueOf(bytes.length));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 샘플 신분증이미지
		if(ObjectUtils.isEmpty(bytes)) {
			log.debug("파일생성실패");
			return;
		}
		
		// 이미지전송 정보세팅
		requestMDLSend.setTxBrn(transactionData.getBranchCode());
		requestMDLSend.setUser(transactionData.getEmployeeNo());
		requestMDLSend.setIp(transactionData.getMetadata().get("ip"));
		requestMDLSend.setIp(transactionData.getMetadata().get("pcid"));
		requestMDLSend.setMidCd(Const.MID_CD_MDL);
		requestMDLSend.setTxDate(transactionData.getCreatedAt().substring(0,8));
		requestMDLSend.setTrxcode(transactionData.getTransactionCode());;
		requestMDLSend.setTxTime(transactionData.getCreatedAt().substring(8,14));
		requestMDLSend.setMidNo(privacy.get(DrivingLicenseVc.ID_NUMBER.getCode()));
		// $$ 진위여부결과코드? step으로 줄지, true/false로 줄지 고민필요 
		requestMDLSend.setMidChk(transaction.getStep().toString());
		

		// ============================ bpr서버와 https 통신 ============================
		// 
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		// 이미지보조원장 정보 전송
		bodyBuilder.part("mdlInfo", requestMDLSend);
		// 파일전송, header 수정필요? 
		bodyBuilder.part("files", new ByteArrayResource(bytes));
		
		// bpr서버에 메타정보와 이미지 전송
		String ret = webClient.mutate()
//				$$ 수정필요, const외의 방법으로 관리?
				.baseUrl("http://localhost:8080")
				.build()
				.post()
				//bpr서버 uri로 수정필요
				.uri("/test/uploadFile")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(bodyBuilder.build()))
				.retrieve()
//				$$ 수정필요, return값은 응답코드3자리 + -(하이픈) + 이미지번호(doc_irn)
				.bodyToMono(String.class)
				// blocking처리(결과를 기다림) - 넣어야할지?
				.block();
		
		String[] strs = ret.split("-");
		
		String status = strs[0];
		// 상태코드가 200이 아니면 exception
		if (!status.equals("200")) {
			throw new CommonException(CommonError.UNKNOWN_ERROR, "send IdCard Image failed");
		}
		
		String docIrn = strs[1];
		// 이미지키 없을 시 exception
		if (Objects.isNull(docIrn) || docIrn.isEmpty()) {
			throw new CommonException(CommonError.UNKNOWN_ERROR, "send IdCard Image failed");
		}
		
		// 상태코드 및 이미지키 변경(성공시) resultCode추가 필요?
		mipTransactionService.update(transactionCode, TransactionUpdateParam.builder().step(Step.COMPLETE).idArchiveInfo(docIrn).build());
	}

	/**
	 * 거래상태/ 처리결과/ 이미지키 응답
	 * @author jhmin
	 * @throws IOException 
	 */
	@Override
	public ResponseData getBizResult(String trxcode) throws IOException {
		// 거래정보조회
		TransactionInfo transaction = mipServiceHelper.getTransactionInfo(trxcode)
				.orElseThrow(() -> new CommonException(CommonError.NOT_FOUND, "Not found",
						new NotFoundDetail("transaction", "trxcode", trxcode)));
		
		// 거래 상태 확인
//		if ((transaction.getStep() != Step.VP_VERIFY && transaction.getStep() != Step.COMPLETE)
//				|| Error.isError(transaction.getResultCode())) {
//			throw new CommonException(CommonError.CONFLICT, String.format("Transaction step=%s result=%d",
//					transaction.getStep(), transaction.getResultCode()));
//		}
		// vp 읽어서 개인정보 조회
//		Map<String, String> privacy = vpService.getPrivacy(trxcode, transaction.getVpArchiveInfo());
		Map<String, String> privacy = vpService.getPrivacy("sample", null); // 샘플 vp
		
		ResponseBizResult responseBizResult = ResponseBizResult.builder()
												.responseTransactionData(ResponseTransactionData.create(transaction))
												.ihidnum(privacy.get("ihidnum"))
												.name(privacy.get("name"))
												.issude(privacy.get("issude"))
												.inspctbegend(privacy.get("inspctbegend"))
												.dlno(privacy.get("dlno"))
												.passwordsn(privacy.get("passwordsn"))
//												.docIrn(transaction.getIdArchiveInfo())
												.docIrn("abc123")
												.build();
		
		return responseBizResult;
	}

	private TransactionInfo getTransaction(String transactionCode) {
		return mipServiceHelper.getTransactionInfo(transactionCode)
				.orElseThrow(() -> new CommonException(CommonError.NOT_FOUND, "trxcode invalid"));
	}
}
