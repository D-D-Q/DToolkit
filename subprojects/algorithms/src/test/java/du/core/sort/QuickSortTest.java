package du.core.sort;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * @author D
 * 
 * 快排测试
 */
public class QuickSortTest {

	@Test
	public void testSort(){
		
		QuickSort quickSort = new QuickSort();
		
		int[] A = {9, 6, 8, 1, 3, 7, 3};
		
		System.out.println(Arrays.toString(A));
		
		quickSort.quickSort(A);
		
		System.out.println(Arrays.toString(A));
	}
}
