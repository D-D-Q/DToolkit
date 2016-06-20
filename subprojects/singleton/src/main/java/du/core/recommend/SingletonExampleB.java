package du.core.recommend;

/**
 * 
 * static对象会在类加载的时候就初始化 JVM会保证static的线程安全
 * jvm启动的时候 不会创建内部类中的static 所以不是恶汉式的
 * 
 * @author D
 *
 * @version 2015年11月18日 上午11:44:01
 */
public class SingletonExampleB {
	
	private  SingletonExampleB() {}
	
	private static class SingletonExampleBHoledr{
		public static SingletonExampleB instance = new SingletonExampleB();
	}
	
	public static SingletonExampleB getInstance(){
		return SingletonExampleBHoledr.instance;
	}
}
