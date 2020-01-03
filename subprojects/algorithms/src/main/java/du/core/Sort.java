package du.core;

/**
 * 排序算法
 * 
 * @author D
 * @date 2017年7月29日 下午4:25:23
 */
public class Sort {

	
	/**
	 * 选择排序
	 * 
	 * 已知一组无序数据a[1]、a[2]、……a[n]，需将其按升序排列。
	 * 首先比较a[1]与a[2]的值，若a[1]大于a[2]则交换两者的值，否则不变。再比较a[1]与a[3]的值，若a[1]大于a[3]则交换两者的值，否则不变。以此类推，
	 * 最后比较a[1]与a[n]的值。这样处理一轮后，a[1]的值一定是这组数据中最小的。
	 * 再将a[2]与a[3]~a[n]以相同方法比较一轮，则a[2]的值一定是a[2]~a[n]中最小的。
	 * 再将a[3]与a[4]~a[n]以相同方法比较一轮，以此类推。
	 * 共处理n-1轮后a[1]、a[2]、……a[n]就以升序排列了。比较次数与冒泡排序一样，数据移动次数比冒泡排序少
	 * 
	 * 时间复杂度是O(N^2); 
	 * 空间复杂度是O(N); 
	 * 
	 * @param A 无序数组
	 */
	public void selectionSort(int[] A) {
		
		for (int i = 0; i < A.length; ++i) {
			for (int j = i + 1; j < A.length; ++j) {
				
				if (A[j] < A[i]) {
					A[j] = A[j] + A[i];
					A[i] = A[j] - A[i];
					A[j] = A[j] - A[i];
				}
			}
		}
	}
	
	/**
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
	 * 
	 * @param A
	 */
	public void quickSort(int[] A){
		
		quickSortSub(A, 0, A.length-1);
	}
	
	public void quickSortSub(int[] A, int start, int end){
		
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
