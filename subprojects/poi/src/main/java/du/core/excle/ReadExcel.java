package du.core.excle;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class ReadExcel {

	/**
	 * 好像XSSFWorkbook取消了
	 * 还有一个SXSSFWorkbook没看看 是xlsx格式的 支持超过65536数据大小
	 * 
	 * 要变成多线程 读多数据
	 * @throws IOException
	 */
	public void read_97_2003() throws IOException {
		
		FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\7月三国杀发码.xls");
		
		HSSFWorkbook wookbook = new HSSFWorkbook(fileInputStream); 
		
		try{
			HSSFSheet sheet = wookbook.getSheetAt(0);
			
			// 行列都是从0开始
			int rows = sheet.getPhysicalNumberOfRows(); // 获取到Excel文件中的所有行数
			
			System.out.println(rows);
			int cells = 3;
			
			for(int i = 0; i < rows; ++i){
				
				HSSFRow row = sheet.getRow(i);
				
				if(row == null) continue;
				
				for(int j = 0; j < cells; ++j){
					
					HSSFCell cell = row.getCell(j);
					
					switch (cell.getCellType()) {
					
						case Cell.CELL_TYPE_STRING:
							
							System.out.println(cell.getStringCellValue());
							break;
							
						case Cell.CELL_TYPE_NUMERIC:
						case Cell.CELL_TYPE_FORMULA:
							
							System.out.println(new BigDecimal(cell.getNumericCellValue()).toPlainString());
							break;
	
						default:
							break;
					}
				}
			}
		}catch(Exception e){
			throw e;
		}finally {
			wookbook.close();
		}
	}
}
