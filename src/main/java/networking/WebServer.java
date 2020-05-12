package networking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.Application;
import app.ScrouchGameLogicApp;
import config.ConfigServer;
import controller.UserStageMonitor;
import htmlAccessories.HtmlBuilder;

public class WebServer {

	private static final String HOME_PAGE_ENDPOINT = "/";
	private static final String UI_ASSETS_BASE_DIR = "ui_assets";
	public static final String RESOURCES_BASE_DIR  = "src/main/resources";

	private final int port;
	private HttpServer server;
	private ScrouchGameLogicApp scrouchLogic;
	private UserStageMonitor userStage;

	public WebServer(int port, ScrouchGameLogicApp scrouchLogic, UserStageMonitor userStage) {
		ConfigServer.initConfig();
		this.port = port;
		this.scrouchLogic = scrouchLogic;
		this.userStage = userStage;
		HtmlBuilder.init(userStage);
	}

	public void startServer() {
		try {
			this.server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		new DataHandlers(this.server, this.scrouchLogic, this.userStage);

		// handle requests for resources
		HttpContext assetsContext = server.createContext(HOME_PAGE_ENDPOINT);
		assetsContext.setHandler(this::handleRequestForAsset);

		server.setExecutor(Executors.newFixedThreadPool(8));
		server.start();
	}

	private void handleRequestForAsset(HttpExchange exchange) throws IOException {
		if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
			exchange.close();
			return;
		}

		byte[] response;

		String asset = exchange.getRequestURI().getPath();

		if (asset.length() < 4 || !asset.substring(asset.length() - 4).contains(".")) {
			String path = UI_ASSETS_BASE_DIR + asset;
			if (asset.equals(HOME_PAGE_ENDPOINT)) {
				String homePagePath = "/" + path + "index.html";;
				Application.logger.info(exchange.getRemoteAddress() + " has connected to website");
				response = readUiAsset(homePagePath);
			} else {
				long userId = Long.parseLong(exchange.getRequestHeaders().get("Cookie").get(0).split("=")[1]);
				path = path + "/index.html";
				response = HtmlBuilder.loadHtml(path, userId);
				Application.logger.info(exchange.getRemoteAddress() + " loaded assest " + path);
//            	response = readUiAsset(path);
			}
		} else {
			response = readUiAsset(asset);
		}
		addContentType(asset, exchange);

		sendResponse(response, exchange);
	}

	private byte[] readUiAsset(String asset) throws IOException  {
		Application.logger.info("asset path: " + asset);
		InputStream assetStream = null;
		if (asset.contains(".png") || asset.contains("jpg")) {
			try {
				assetStream = new FileInputStream(/*RESOURCES_BASE_DIR + */asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				Application.logger.error("asset not found: " + e);
				return new byte[] {};
			}
			
		}
		else {
			assetStream = getClass().getResourceAsStream(asset);
			if (assetStream == null) {
				Application.logger.error("asset not found: from getResource");
				return new byte[] {};
			}
			
		}
	
		Application.logger.info("assest read: " + asset);
		byte[] stream = assetStream.readAllBytes();
		assetStream.close();
		return stream;
	}

	static void addContentType(String asset, HttpExchange exchange) {
		String contentType = "text/html";
		if (asset.endsWith("js")) {
			contentType = "text/javascript";
		} else if (asset.endsWith("css")) {
			contentType = "text/css";
		}
		exchange.getResponseHeaders().add("Content-Type", contentType);
	}

	static void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(200, responseBytes.length);
		OutputStream outputStream = exchange.getResponseBody();
		try {
			outputStream.write(responseBytes);
			
		}catch (IOException e) {
			System.out.println("EXCEPTION: " + e);
		}
		outputStream.flush();
		outputStream.close();
	}
}
