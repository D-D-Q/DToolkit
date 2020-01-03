package du.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

	// 注意LoggerFactory和Logger是slf4j的
	private final static Logger log = LoggerFactory.getLogger("test1");
	
	public void doSomething() {
		
		log.trace("log {}", "trace"); //据说{}这种方式连接字符串 比直接+ 要快47倍
		log.debug("log {}", "debug");
		log.info("log {}", "info");
		log.warn("log {}", "warn");
		log.error("log {}", "error");
//		log.fatal("log {}", "fatal"); log4j有 slf4j没有
	}
}
