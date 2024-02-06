package board;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class jhminTest {

	public static void main(String[] args) throws IOException {
		//엑셀 생성
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("tr정보");
		
		int rowCount = 0;
		String headerNames[] = {"가","나","다"};
		Row headerRow =null;
		Cell headerCell = null;
		
		headerRow = sheet.createRow(rowCount++);
		for(int i=0; i<headerNames.length; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(headerNames[i]);
		}
		
		String bodyDatass[][] = {{"11","12","13"},{"21","22","23"},{"31","32","33"}};
		Row bodyRow = null;
		Cell bodyCell = null;
		
		for(String[] bodyDatas : bodyDatass) {
			bodyRow = sheet.createRow(rowCount++);
			
			for(int i=0; i<bodyDatas.length; i++) {
				bodyCell = bodyRow.createCell(i);
				bodyCell.setCellValue(bodyDatas[i]);
			}
		}
		
		String tempPath = "C:/local_temp/";
		String path = tempPath + "abc.xlsx";
		
		FileOutputStream fos = new FileOutputStream(path);
		workbook.write(fos);
		workbook.close();
		
		//엑셀읽기
		String path2 = "C:/local_temp/abc.xlsx";
		Workbook workbook2 = new XSSFWorkbook(path2);
		
		Sheet worksheet = workbook2.getSheetAt(0);
		
		for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++) {
			
			Row row = worksheet.getRow(i);
			
			if(row != null) {
				for(int j=0; j<=2; j++) {
					Cell cell = row.getCell(j);
					if(cell == null) {
						System.out.println("null");
					} else {
						System.out.println(cell.getStringCellValue());
					}
				}
			}
			
		}
		
		workbook2.close();
	}
}
