package algorithms;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import du.core.Sort;

public class SortTest {
	
	private Sort sort = new Sort();

	@Test
	public void testSort(){
		
		int[] A = {9, 6, 8, 1, 3, 7, 3};
		
		System.out.println(Arrays.toString(A));
		
//		selectionSort(A);
		sort.quickSort(A);
		
		System.out.println(Arrays.toString(A));
	}
}
