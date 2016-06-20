package du.core.recommend;

/**
 * 
 * 饿汉式 启动就把对象创建了 不等用的时候创建
 * 
 * @author D
 *
 * @version 2015年11月27日 下午12:33:31
 */
public class SingletonExampleC {
	
	public static SingletonExampleC instance = new SingletonExampleC();
	
    public static SingletonExampleC instance() {  
    	
        return instance;  
    }

}
