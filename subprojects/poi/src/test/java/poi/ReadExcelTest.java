package poi;

import org.junit.jupiter.api.Test;

import du.core.excle.ReadExcel;

public class ReadExcelTest {
	
	private ReadExcel readExcel = new ReadExcel();
	
	
	@Test
	public void testRead_97_2003() throws Exception {
		
		readExcel.read_97_2003();
		
	}
}
