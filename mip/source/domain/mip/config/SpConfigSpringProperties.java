package com.inzisoft.mobileid.sp.domain.mip.config;

import com.inzisoft.mobileid.sdk.mip.config.SpConfig;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(ignoreResourceNotFound = true,
		value = {"classpath:config/sp.properties",
		"file:config/sp.properties" })
@Component
@Getter
@ToString
public class SpConfigSpringProperties implements SpConfig {
	//sp 서버 설정
	@Value("${app.sp-imageurl}")
	private String spImageurl;
	@Value("${app.sp-image-base64}")
	private String spImageBase64;
	@Value("${app.sp-server}")
	private String spServer;
	// 블록체인 RCP HOST
	@Value("${app.blockchain-server-domain}")
	private String blockchainServerDomain;
	//Wallet 파일 세팅
	@Value("${app.keymanager-path}")
	private String keymanagerPath;
	// Wallet 패스워드
	@Value("${app.keymanager-password}")
	private String keymanagerPassword;
	//sp mainKeyId
	@Value("${app.sp-key-id}")
	private String spKeyId;
	//SP RSA keyId
	@Value("${app.sp-rsa-key-id}")
	private String spRsaKeyId;
	// 통신구간 ECIES VC암호화 여부
	@Value("${app.vc-encrypt-type:false}")
	private boolean vcEncryptType = false;
	// SP 블록체인 계정
	@Value("${app.sp-account}")
	private String spAccount;
	// SP DID 파일 경로
	@Value("${app.sp-did-path}")
	private String spDidPath;
	// SP Service Code
	@Value("${app.default-service-code}")
	private String defaultServiceCode;

	// 라이선스 파일 경로
	@Value("${app.license-file-path:null}")
	private String lincenseFilePath;
	// SHA 엔디언 변경
	@Value("${app.convert-sha-litten-endian:false}")
	private boolean convertShaLittenEndian = false;

	// SDK 로그 상세 출력 여부
	@Value("${app.sdk-detail-log}")
	private boolean sdkDetailLog = false;
	// 블록체인 조회 캐싱 사용 여부
	@Value("${app.sdk-use-cache}")
	private boolean sdkUseCache = false;

	// ZKP schema
	@Value("${app.zkp-schema-name}")
	private String zkpSchemaName;

	// PUSH
	@Value("${app.push-ms-code}")
	private String pushMsCode;
	@Value("${app.push-server-domain}")
	private String pushServerDomain;

	// proxy server
	@Value("${app.proxy-server}")
	private String proxyServer;
}
