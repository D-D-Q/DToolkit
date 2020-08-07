package du.core.sort;

/**
 * @author D
 * 
 * 快速排序
 * 
 * 先从数列中取出一个数作为基准数。
 * 分区过程，将比这个数大的数全放到它的右边，小于或等于它的数全放到它的左边。
 * 再对左右区间重复第二步，直到各区间只有一个数。
 * 
 * i =L; j = R; 将基准数挖出形成第一个坑a[i]。
 * j--由后向前找比它小的数，找到后挖出此数填前一个坑a[i]中。
 * i++由前向后找比它大的数，找到后也挖出此数填到前一个坑a[j]中。
 * 再重复执行前两步，直到i==j，将基准数填入a[i]中。
 * 
 * 最优时间复杂度是O(N*logN); 每次取的基准数都能平分数组的情况下
 * 最差时间复杂度是O(N^2); 每次取的基准数都是最大或最小的值
 * 最优空间复杂度是O(logN); 每次取的基准数都能平分数组的情况下
 * 最差空间复杂度是O(N); 每次取的基准数都是最大或最小的值
 * 
 */
public class QuickSort {

	/**
	 * @param A 无序数组
	 */
	public void quickSort(int[] A){
		
		quickSortSub(A, 0, A.length-1);
	}
	
	private void quickSortSub(int[] A, int start, int end){
		
		if(start >= end)
			return;
		
		int i = start, j = end;
		int key = A[i];
		
		while(i < j){
			
			while(i < j){
				if(A[j] < key){
					A[i++] = A[j];
					break;
				}
				--j;
			}
			
			while(i < j){
				if(A[i] > key){
					A[j--] = A[i];
					break;
				}
				++i;
			}
			
			A[i] = key; 
			
			quickSortSub(A, start, i-1);
			quickSortSub(A, i+1, end);
		}
	}
}
