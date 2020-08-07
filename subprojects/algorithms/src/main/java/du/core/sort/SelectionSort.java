package du.core.sort;

/**
 * @author D
 * 
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
 */
public class SelectionSort {

	/**
	 * @param A 无序数组
	 */
	public void sort(int[] A) {
		
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
}