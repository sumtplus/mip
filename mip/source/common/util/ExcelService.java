package com.inzisoft.mobileid.sp.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ExcelService {

	void downloadExcelFile(HttpServletResponse response,
			String filename, String title, String[] headers, String[] bodyFields, List<Object> datas);

	byte[] createExcelFile(String title, String[] headers, String[] bodyFields, List<Object> datas) throws Exception;

	List<Map<String, String>> readMapListFromExcelFile(InputStream excelInputStream);
}
