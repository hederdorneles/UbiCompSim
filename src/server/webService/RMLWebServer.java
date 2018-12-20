package server.webService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import server.webService.contextualPlanning.ExecutionServlet;
import server.webService.contextualPlanning.GetResourcesServlet;

public class RMLWebServer {

	public void start() throws Exception {
		Server server = new Server(8080);
		// server.setHandler(new HelloHandler());
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase(System.getProperty("java.io.tmpdir"));
		context.addServlet(GetResourcesServlet.class, "/requisition/*");
		context.addServlet(ExecutionServlet.class, "/execution/*");
		context.addServlet(DefaultServlet.class, "/status");
		context.addServlet(DefaultServlet.class, "/");
		server.setHandler(context);

		server.start();
		server.join();
	}

}
