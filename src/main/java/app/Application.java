package app;

import java.io.IOException;

import org.apache.log4j.Logger;

import controller.UserStageMonitor;
import networking.WebServer;

public class Application {

	public final static Logger logger = Logger.getLogger(Application.class);
	
	private static final int DEFAULT_PORT = 9000;
	

	public static void main(String[] args) throws IOException {
		int port = DEFAULT_PORT;
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		
		ScrouchGameLogicApp scrouchLogic = new ScrouchGameLogicApp();
		UserStageMonitor userStage = new UserStageMonitor();

		WebServer webServer = new WebServer(port, scrouchLogic, userStage);

		webServer.startServer();
		
		Application.logger.warn("Server is listening on port " + port);
	}

}
