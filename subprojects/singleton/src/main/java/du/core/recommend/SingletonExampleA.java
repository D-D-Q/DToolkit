package du.core.recommend;

/**
 * 单线程程序使用
 * 
 * 多线程并发使用时，会出现创建多个对象的情况。
 * 因为线程调度没有保证。比如A线程进入了if判断还没有new对象CPU就切换到了B线程，B线程也会进入if判断。这样可能有多个线程可以执行到创建对象的代码，并都创建了对象。
 * 多线程单例 {@SingletonExampleB}
 * 
 * @author D
 *
 */
public class SingletonExampleA {

	private SingletonExampleA(){}
	
	private static SingletonExampleA instance;
	
	public static SingletonExampleA getInstance(){
		
		if(instance == null){
			instance = new SingletonExampleA();
		}
		
		return instance;
	}
}
