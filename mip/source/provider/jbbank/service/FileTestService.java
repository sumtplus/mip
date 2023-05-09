package com.inzisoft.mobileid.provider.jbbank.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 테스트하기 위한 서비스
 * @author jhmin
 *
 */
public interface FileTestService {
	String uploadFile(MultipartHttpServletRequest request) throws Exception;
}
