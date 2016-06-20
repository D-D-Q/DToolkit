package du.core.excle;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class WriteExcle {

	public void wirte() {
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		sheet.setColumnWidth(0, 16 * 256);
		
		Cell cell = sheet.createRow(0).createCell(0);
		
		cell.setCellValue("");
		
//		workbook.write(outputStream);
	}
}
