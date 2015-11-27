package du.core.deprecated;

/**
 * 
 * 双检锁单例 线程安全的单例 但是使用了synchronized关键字 可能会性能
 * {@link du.core.recommend.SingletonExampleB}
 * 
 * @author D
 *
 * @version 2015年11月18日 上午11:50:34
 */
public class SingletonExampleB {  
	
    private static SingletonExampleB instance = null;  
  
    public static SingletonExampleB instance() {
    	
        if (instance == null) {
        	
            synchronized (SingletonExampleB.class) {  
            	
                if (instance == null)
                    instance = new SingletonExampleB();  
            }
        }
        
        return instance;  
    }  
}