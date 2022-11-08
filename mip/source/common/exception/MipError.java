package com.inzisoft.mobileid.sdk.code.error;

import com.inzisoft.mobileid.common.exception.ErrorInterface;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;

@Slf4j
public enum MipError implements ErrorInterface {
	OK(0, "OK"),

	/* Verifier Error */
	SP_UNEXPECTED_MSG_FORMAT(10001, "unexpected message format"), // JSON 형식의 메시지가 아님
	SP_MISSING_MANDATORY_ITEM(10002, "missing mandatory item"), // 필수 항목 누락
	SP_BASE64_DECODE_ERROR(10003, "base64 decode error"), // Base64 Decode Error
	// 데이터
	SP_INVALID_DATA(10100, "invalid data"), // 유효하지 않은 항목 데이터
	SP_INVALID_TYPE(10101, "invalid type"), // 유효하지 않은 type("mip" 가 아님)
	SP_UNSUPPORTED_MSG_VERSION(10102, "unsupported message version"), // 지원하지 않는 메시지 버전
	SP_INVALID_CMD(10103, "invalid cmd"), // 유효하지 않은 cmd
	SP_TRXCODE_NOT_FOUND(10104, "trxcode not found"), // 존재하지 않는 거래코드
	SP_PROFILE_CREATE_ERROR(10105, "profile create error"), // Profile 생성 오류
	SP_UNSUPPORTED_VP_PRESENT_TYPE(10106, "unsupported VP presentType"), // 지원하지 않는 VP presentType
	SP_UNSUPPORTED_VP_ENC_TYPE(10107, "unsupported VP encryptType"), // 지원하지 않는 VP encryptType : MDL 은 AES-256 사용
	SP_UNSUPPORTED_VP_KEY_TYPE(10108, "unsupported VP keyType"), // 지원하지 않는 VP keyType : MDL 은 RSA 사용
	SP_UNSUPPORTED_VP_TYPE(10109, "unsupported VP type"), // 지원하지 않는 VP Type : MDL 은 "VERIFY" 사용
	SP_UNSUPPORTED_VP_AUTH_TYPE(10110, "unsupported VP authType"), // 지원하지 않는 VP authType : pin, bio, face
	SP_MISMATCHING_NONCE(10111, "mismatching nonce"), // nonce 불일치(profile vs vp)
	SP_MISMATCHING_AUTH_TYPE(10112, "mismatching authType"), // authType 불일치(profile vs vp)
	// 절차
	SP_MSG_SEQ_ERROR(10201, "message sequence error"), // 메시지 전송 순서 오류
	SP_TIMEOUT_ERROR(10202, "timeout error"), // 유효시간 초과 오류

	SP_DB_ERROR(10301, "database error"), // Database 오류

	/* Mobile Error */
	// 형식
	MB_UNEXPECTED_MSG_FORMAT(20001, "unexpected message format"), // JSON 형식의 메시지가 아님
	MB_MISSING_MANDATORY_ITEM(20002, "missing mandatory item"), // 필수 항목 누락
	// 데이터
	MB_INVALID_DATA(20100, "invalid data"), // 유효하지 않은 항목 데이터
	MB_INVALID_RESULT(20100, "invalid result"), // result가 true/false가 아님
	MB_ENCODING_ERROR(20102, "encoding error"), // 데이터가 정상적으로 잍코딩 되지 않음
	MB_MISMATCHING_TRXCODE(20103, "mismatching trxcode"), // 거래코드 불일치
	MB_UNSUPPORTED_IMAGE_TYPE(20104, "unsupported image type"), // 지원하지 않는 이미지 타입
	// 절차
	MB_MSG_SEQ_ERROR(20201, "message sequence error"), // 메시지 전송 순서 오류
	MB_TIMEOUT_ERROR(20202, "timeout error"), // 유효시간 초과
	// Profile
	MB_INVALID_PROFILE_DATA(20300, "invalid profile data"), // profile 내 유효하지 않은 항목 데이터
	MB_VC_NOT_EXISTS(20301, "VC not exists"), // 발급받은 VC 없음
	MB_NO_VC_ALLOWED_ISSUERS(20302, "no VC for allowed issuers"), // 요청된 발급자용 VC 없음
	MB_MISSING_MANDATORY_ITEM_IN_PROFILE(20303, "missing mandatory item in profile"), // profile 내 필수 항목 누락
	MB_MISSING_NONCE_IN_PROFILE(20304, "missing nonce in profile"), // profile 내 nonce 없음
	MB_MISSING_PROOF_IN_PROFILE(20305, "missing proof in profile"), // profile 내 proof 없음
	MB_PROFILE_SIGNATURE_VALIDATION_FAIL(20306, "profilesignaturevalidation fail"), // profile 내 서명 검증 실패
	MB_FAIL_GET_DID_DOCUMENT(20307, "fail to get DID document"), // DID document 조회 실패
	// 영지식
	MB_ZKP_ERROR_1(20401, "ZKP error 1"), // 제출 정보 검색 실패(fail to search referent)
	MB_ZKP_ERROR_2(20402, "ZKP error 2"), // 나이 조건 불일치
	MB_ZKP_ERROR_3(20403, "ZKP error 3"), // Credential def, schema data 조회 실패
	// 기타
	MB_USER_CANCEL(20901, "cancel by user"), // 사용자에 의한 취소
	MB_USER_AUTH_FAIL(90001, "user authentication fail"), // 사용자 인증 실패
	MB_INVALID_APP_VERSION(90002, "Invalid app version"), // 유효하지 않은 앱 버전

	// 공통
	UNKNOWN_ERROR(99999, "Unknown error");

	int code;
	@Getter
	String message;

	MipError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String getCode() {
		return String.valueOf(code);
	}

	public static MipError fromCode(int errorCode) {
		return EnumSet.allOf(MipError.class).stream()
				.filter(error -> error.code == errorCode)
				.findFirst().orElse(MipError.UNKNOWN_ERROR);
	}
}
