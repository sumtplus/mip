package com.inzisoft.mobileid.sp.common.util;

import com.inzisoft.mobileid.common.exception.CommonError;
import com.inzisoft.mobileid.common.exception.CommonException;
import com.inzisoft.mobileid.common.util.CommonUtil;
import com.inzisoft.mobileid.sp.domain.code.repository.CodeRepository;
import com.inzisoft.mobileid.sp.domain.code.repository.MipCodeId;
import com.inzisoft.mobileid.sp.domain.service.repository.MipServiceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelServiceImpl implements ExcelService {
	private static Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);

	private final MipServiceRepository mipServiceRepository;
	private final CodeRepository codeRepository;
	public ExcelServiceImpl(MipServiceRepository mipServiceRepository, CodeRepository codeRepository){
		this.mipServiceRepository = mipServiceRepository;
		this.codeRepository = codeRepository;
	}

	@Override
	public void downloadExcelFile(HttpServletResponse response, String filename,
			String title, String[] headers, String[] bodyFields, List<Object> datas) {
		try {
			logger.info("Excel download ");
			byte[] excelBytes = createExcelFile(title, headers, bodyFields, datas);

			response.setContentLength(excelBytes.length);
			response.setContentType("application/vnd.mx-excel; charset=UTF-8");

			response.setHeader("Content-Disposition", "attachment; filename=\"" + title + ".xlsx\";");
			response.setHeader("Content-Transfer-Encoding", "binary");

			OutputStream out = response.getOutputStream();
			out.write(excelBytes);
			out.flush();
			out.close();
		} catch(Exception e) {
			logger.error("Download excel failed, {}", e.getMessage());
		}
	}

	/**
	 * 현재는 엑셀 파일 사이즈가 크지 않을 것으로 예상되어 byte[] 로 컨트롤 하도록 하고 있음
	 * byte[] 로 만들면 총 길이를 알 수 있기 때문에 header에 length 값을 지정할 수 있음
	 * 데이터가 많아질 경우 byte[] 방식으로 데이터를 컨트롤하면 메모리 사용량이 많아지므로
	 * Response output stream에 바로 쓰는 방식으로 변경하는 것을 권장함
	 */

	public byte[] createExcelFile(String title, String[] headers, String[] bodyFields, List<Object> datas) throws Exception {
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		logger.info("Create excel file");
		try {
			SXSSFSheet sheet = wb.createSheet();
			sheet.trackAllColumnsForAutoSizing();
			wb.setSheetName(0, "sheet1");

			int rowNumber = 0;
			rowNumber = writeTitle(sheet, getTitleCellStyle(wb), rowNumber, headers.length-1, title);
			rowNumber = writeHeader(sheet, getHeaderCellStyle(wb), rowNumber, headers);
			rowNumber = writeBody(sheet, getBodyCellStyle(wb), rowNumber, bodyFields, datas);
			for(int col=0; col<headers.length; col++) {
				sheet.autoSizeColumn(col); // 열 너비 자동 조정
			}
			wb.write(baos);
			return baos.toByteArray();
		} catch (Exception e) {
			logger.error("Create excel failed, {}", e.getMessage());
			throw new Exception(e);
		} finally {
			if(wb != null) {
				wb.dispose();
			}
			if(baos != null) {
				try { baos.close(); } catch(Exception e) { }
			}
		}
	}

	@Override
	public List<Map<String, String>> readMapListFromExcelFile(InputStream excelInputStream) {
		XSSFWorkbook workbook = null;
		logger.info("Read map list from excel File");
		try {
			workbook = new XSSFWorkbook(excelInputStream);

			Map<Integer, String> colHeaderInfoMap = new HashMap<Integer, String>();
			List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>();

			Sheet sheet = workbook.getSheetAt(0);
			for(int rowIndex=0; rowIndex<sheet.getPhysicalNumberOfRows(); rowIndex++) {
				if(rowIndex == 0) { // 첫번째 줄은 타이틀이므로 무시

				} else if(rowIndex == 1) { // 두번째 줄은 헤더 데이터
					Row row = sheet.getRow(rowIndex);
					for(int colIndex=0; colIndex<row.getPhysicalNumberOfCells(); colIndex++) {
						Cell cell = row.getCell(colIndex);
						String value = cell.getStringCellValue();
						if(!CommonUtil.isNullOrEmpty(value)) {
							colHeaderInfoMap.put(colIndex, cell.getStringCellValue());
						}
					}
				} else {
					Row row = sheet.getRow(rowIndex);
					Map<String, String> rowInfoMap = new HashMap<String, String>();
					for(Integer colNum : colHeaderInfoMap.keySet()) {
						Cell cell = row.getCell(colNum);
						String value = cell.getStringCellValue();
						if(!CommonUtil.isNullOrEmpty(value)) {
							rowInfoMap.put(colHeaderInfoMap.get(colNum), value);
						}
					}
					dataMapList.add(rowInfoMap);
				}
			}
			return dataMapList;
		} catch(Exception e) {
			throw new CommonException( CommonError.UNKNOWN_ERROR, "Read excel file failed, " + e.getMessage());
		} finally {
//			if(bais != null) {
//				try { bais.close(); } catch(Exception e) {};
//			}
		}
	}

	private int writeTitle(Sheet sheet, CellStyle cellStyle, int rowNumber, int titleColMergeSize, String title) {
		Row row = sheet.createRow(rowNumber);
		Cell cell = row.createCell(rowNumber);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(title);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, titleColMergeSize));
		return ++rowNumber;
	}

	private int writeHeader(Sheet sheet, CellStyle cellStyle, int rowNumber, String[] headers) {
		Row row = sheet.createRow(rowNumber);
		for(int col=0; col<headers.length; col++) {
			Cell cell = row.createCell(col);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(headers[col]);
		}
		return ++rowNumber;
	}

	private int writeBody(Sheet sheet, CellStyle cellStyle, int rowNumber, String[] bodyFields, List<Object> datas) {
		for(int dataIdx=0; dataIdx<datas.size(); dataIdx++) {
			Row row = sheet.createRow(rowNumber);
			Object data = datas.get(dataIdx);
			Map<String,String> values = getObjectFieldDatas(data, bodyFields);
			String changeValue = "";
			for(int col=0; col<values.size(); col++) {
				Cell cell = row.createCell(col);
				cell.setCellStyle(cellStyle);
				switch (bodyFields[col]){
					case "serviceCode":
						changeValue = mipServiceRepository.findById(values.get(bodyFields[col])).get().getName();
						break;
					case "resultCode":
						if(values.get(bodyFields[col]).equals("0")){
							changeValue = "true";
						}else{
							changeValue = "false";
						}
						break;
					case "idType":
						changeValue = codeRepository.findById( MipCodeId.create("ID_TYPE", values.get(bodyFields[col]))).get().getDescription();
						break;
					default:
						changeValue = values.get(bodyFields[col]);
						break;
				}
				cell.setCellValue(changeValue);
			}

			++rowNumber;
		}
		return rowNumber;
	}

	private CellStyle getTitleCellStyle(SXSSFWorkbook wb) {
		Font titleFont = wb.createFont();
		titleFont.setFontName("Arial");
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short)14);
		titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());

		CellStyle titleCellStyle = wb.createCellStyle();
		titleCellStyle.setFont(titleFont);
		titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

		return titleCellStyle;
	}

	private CellStyle getHeaderCellStyle(SXSSFWorkbook wb) {
		Font headerFont = wb.createFont();
		headerFont.setFontName("malgun gothic");
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short)12);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		return headerCellStyle;
	}

	private CellStyle getBodyCellStyle(SXSSFWorkbook wb) {
		Font bodyFont = wb.createFont();
		bodyFont.setFontName("malgun gothic");
		bodyFont.setBold(false);
		bodyFont.setFontHeightInPoints((short)10);

		CellStyle bodyCellStyle = wb.createCellStyle();
		bodyCellStyle.setFont(bodyFont);
		bodyCellStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyCellStyle.setBorderBottom(BorderStyle.THIN);
		bodyCellStyle.setBorderRight(BorderStyle.THIN);

		return bodyCellStyle;
	}

	private Map<String,String> getObjectFieldDatas(Object object, String[] bodyFields) {
		Map<String,String> values = new HashMap<String,String>();
		Class<?> objectClass = object.getClass();
		Field[] fields = objectClass.getDeclaredFields();
		for(Field field : fields) {
			for(String bodyField: bodyFields) {
				try {
					if(bodyField.equalsIgnoreCase(field.getName())) {
						field.setAccessible(true);
						logger.info("Field: {} Value: {}", field.getName(), field.get(object));
						Object fieldObject = field.get(object);
						values.put(bodyField, (fieldObject==null?"":fieldObject.toString()));
					}
				} catch (Exception e) {
					logger.error("Member {} get value failed", field.getName());
					values.put(bodyField, "");
				}
			}
		}
		return values;
	}
}
