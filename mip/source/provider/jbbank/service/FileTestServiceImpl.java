package com.inzisoft.mobileid.provider.jbbank.service;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileTestServiceImpl implements FileTestService{

	public FileTestServiceImpl() {};
	
	public String uploadFile(MultipartHttpServletRequest request) throws Exception {
		String docIrn = "";
		String mdlInfo = request.getParameter("mdlInfo");
		if(!ObjectUtils.isEmpty(mdlInfo)) {
			log.debug("mdlInfo : ["+ mdlInfo+"]");
		}
		
		int ret = parseFileInfo(request);
		
		if(ret != 0) {
			docIrn = "abc123"; 
		}
		
		return docIrn;
	}

	public int parseFileInfo(MultipartHttpServletRequest request) throws Exception{
		if(ObjectUtils.isEmpty(request)) {
			log.debug("파일이 없습니다.");
			return 1;
		}
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZonedDateTime current = ZonedDateTime.now();
		String uuid = UUID.randomUUID().toString();
		String path = "C:\\upload" + File.separator + current.format(format);
		File file = new File(path);
		
		if(!file.exists()) {
			log.debug("make folder");
			file.mkdirs();
		}
		
		String newFileName;
		String ext;
		
		Iterator<String> iterator = request.getFileNames();
		
		while(iterator.hasNext()) {
			List<MultipartFile> list = request.getFiles(iterator.next());
			for(MultipartFile multipartFile : list) {
				if(!multipartFile.isEmpty()) {
					log.debug("============= file information =============");
					log.debug("file name : "+multipartFile.getOriginalFilename());
					log.debug("file size : "+multipartFile.getSize());
					log.debug("file content type : "+multipartFile.getContentType());
					
					ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
					
					newFileName = Long.toString(System.nanoTime()) + "." + ext;
					
					file = new File(path + "/" + newFileName);
					multipartFile.transferTo(file);
					
				}
			}
		}
		
		log.debug("파일저장완료");
		return 0;
	}
}
