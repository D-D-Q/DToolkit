package d.cgunit.core.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import d.cgunit.core.CGUnitServer;

public class CGUnitServletContextListener implements ServletContextListener {

	private CGUnitServer cgunitServer;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
//		String port = sce.getServletContext().getInitParameter("cgunit.port");
//		if(port == null)
			cgunitServer = new CGUnitServer();
//		else
//			cgunitServer = new CGUnitServer(Integer.parseInt(port));
		
		cgunitServer.startServer();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		cgunitServer.stopServer();
	}
	
	
}
