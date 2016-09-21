package d.cgunit.core;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * 测试过程记录
 * 
 * @author D
 */
public class TestRunListener extends RunListener {
	
	private String layout = "---------------------------------------------------------------------------------------";

    /**
     * 所有测试开始
     */
    public void testRunStarted(Description description) throws Exception {
    	System.out.println(layout);
    	System.out.println("Start test case. ");
    	System.out.println(layout);
    }
    
    /**
     * 所有测试完成
     */
    public void testRunFinished(Result result) throws Exception {
    	System.out.println(layout);
    	System.out.println("Finish test case. time: " + result.getRunTime() + "ms. " + "fails/total: " + result.getFailureCount() + "/" + result.getRunCount());
    	if(result.getFailureCount() != 0){
    		System.out.println("Fails method:");
    		for(Failure failure : result.getFailures())
    			System.out.println("	" + failure.toString());
    	}
    	System.out.println(layout);
    }
    
    /**
     * 一个@Test执行开始
     */
    public void testStarted(Description description) throws Exception {
    	System.out.println(layout);
    	System.out.println("Start test case method: " + description.getDisplayName());
    }

    /**
     * 一个@Test测试错误，因为断言或异常
     */
    public void testFailure(Failure failure) throws Exception {
    	System.err.println("Failure Test case method: " + failure.getDescription().getDisplayName() + "\n" + failure.getTrace());
    }
    
    /**
     * 一个@Test执行完成，不管成功失败
     */
    public void testFinished(Description description) throws Exception {
    	System.out.println("Finish Test case method: " + description.getDisplayName());
    	System.out.println(layout);
    }

    public void testAssumptionFailure(Failure failure) {
    }

    public void testIgnored(Description description) throws Exception {
    }
}
