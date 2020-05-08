package networking;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.Application;
import app.ScrouchGameLogicApp;
import controller.UserStageMonitor;
import htmlAccessories.HtmlData;
import logic.DrawingTrueSentencePair;
import logic.PlayerPersonalInfo;
import logic.Result;
import model.frontend.FrontendRequest;
import model.frontend.FrontendResponse;

public class DataHandlers {

	private static final String DATA_ENDPOINT = "/data";
	private static final String CREATE_GAME_ENDPOINT = "/create_game";
	private static final String JOIN_GAME_ENDPOINT = "/join_game";
	private static final String WAITING_ROOM_ENDPOINT = "/waiting_room";
	private static final String PERSONAL_INFO_ENDPOINT = "/personal_info";
	private static final String DRAWING_ENDPOINT = "/drawing";
	private static final String FALSING_ENDPOINT = "/falsing";
	private static final String GUESSING_ENDPOINT = "/guessing";
	private static final String RESULTS_ENDPOINT = "/results";
	private static final String SCORES_ENDPOINT = "/scores";
	private static final String ANOTHER_GAME_ENDPOINT = "/another_game";
	private static final String WINNER_ENDPOINT = "/winner";
	private static final String PLAYERS_PRESENTATION_ENDPOINT = "/players_presentation";
	private static final String NOT_ENOUGH_PLAYERS_ENDPOINT = "/not_enough_players";

	private HttpServer server;
	private ScrouchGameLogicApp scrouchLogic;
	private UserStageMonitor userStage;
	private ObjectMapper objectMapper;

	public DataHandlers(HttpServer server, ScrouchGameLogicApp scrouchLogic, UserStageMonitor userStage) {
		// TODO Auto-generated constructor stub
		this.server = server;
		this.scrouchLogic = scrouchLogic;
		this.userStage = userStage;
		this.objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

		// handle requests for resources
		HttpContext assetsContext = this.server.createContext(DATA_ENDPOINT);
		assetsContext.setHandler(this::handleRequestData);
	}

	private void handleRequestData(HttpExchange exchange) throws IOException {
		if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
			exchange.close();
			return;
		}

		FrontendRequest data = objectMapper.readValue(exchange.getRequestBody().readAllBytes(), FrontendRequest.class);
		byte[] response = null;

		String asset = exchange.getRequestURI().getPath();

		switch (asset.replaceAll(DATA_ENDPOINT, "")) {
		case CREATE_GAME_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " request to create game with num of players = "
					+ data.getNumOfPlayers() + ", sessions = " + data.getNumOfSessions());
			String gameCode = scrouchLogic.newGame(data.getNumOfPlayers(), data.getNumOfSessions());
			userStage.resetBarriersToClosed(gameCode);
			int playersLeft = scrouchLogic.joinPlayerToGame(data.getUserId(), gameCode);
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_JOINERS, gameCode);
			Application.logger.info(htmlData + ". left " + playersLeft + " players");
			userStage.addUser(data.getUserId(), gameCode);
			userStage.setStageData(data.getUserId(), htmlData);
			if (playersLeft == 0) {
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			response = objectMapper.writeValueAsBytes("");

			break;
		}
		case JOIN_GAME_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " request to join game " + data.getGameCode());
			Long id1 = Long.parseLong("880398900877000300");
			Long id2 = Long.parseLong("481654656413539800");
			String gameCode = userStage.getGameCode(data.getUserId() == id1 ? id2 : id1);
			int playersLeft = scrouchLogic.joinPlayerToGame(data.getUserId(), /* data.getGameCode() */gameCode);
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_JOINERS,
					/* data.getGameCode() */gameCode);
			Application.logger.info(htmlData + ". left " + playersLeft + " players");
			userStage.addUser(data.getUserId(), /* data.getGameCode() */gameCode);
			userStage.setStageData(data.getUserId(), htmlData);
			if (playersLeft == 0) {
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			response = objectMapper.writeValueAsBytes("");

			break;
		}
		case WAITING_ROOM_ENDPOINT: {
			Application.logger.info("request to check if continue");
			boolean isContinue = userStage.getUserContinueNextStage(data.getUserId());
			String urlRedirection = null;
			if (isContinue) {
				Application.logger.info(data.getUserId() + " ok to continue");
				switch (userStage.getStage(data.getUserId())) {
				case UserStageMonitor.WAITING_ROOM_JOINERS: {
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.PERSONAL_INFO);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = PERSONAL_INFO_ENDPOINT;
					break;
				}
				case UserStageMonitor.WAITING_ROOM_REGISTERING_INFO: {
					List<PlayerPersonalInfo> playersInfo = scrouchLogic
							.getAllPlayersInformation(userStage.getGameCode(data.getUserId()));
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.PLAYERS_PRESENTATION,
							playersInfo);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = PLAYERS_PRESENTATION_ENDPOINT;
					break;
				}
				case UserStageMonitor.PLAYERS_PRESENTATION: {
					String trueSentence = scrouchLogic.getNextTrueSentence(userStage.getGameCode(data.getUserId()));
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.DRAWING, trueSentence);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = DRAWING_ENDPOINT;
					break;
				}
				case UserStageMonitor.WAITING_ROOM_DRAWING: {
					DrawingTrueSentencePair drawingSentence = scrouchLogic
							.getCurrentDrawingSentencePlayer(userStage.getGameCode(data.getUserId()));
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.FALSING, drawingSentence);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = FALSING_ENDPOINT;
					if (drawingSentence.getPlayerId() == data.getUserId()) {
						urlRedirection = WAITING_ROOM_ENDPOINT;
					}
					break;
				}
				case UserStageMonitor.WAITING_ROOM_FALSING: {
					List<String> allSentences = scrouchLogic
							.getAllSentencesToCurrentDrawingExceptFalser(data.getUserId());
					Application.logger.info("all sentences: " + allSentences);
					DrawingTrueSentencePair drawingSentence = scrouchLogic
							.getCurrentDrawingSentencePlayer(userStage.getGameCode(data.getUserId()));
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.GUESSING, allSentences,
							drawingSentence);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = GUESSING_ENDPOINT;
					break;
				}
				case UserStageMonitor.WAITING_ROOM_GUESSING: {
					Application.logger.info(data.getUserId() + " on waiting for guessing handler");
					List<Result> resultsCurrentRound = scrouchLogic
							.getCurrentDrawingResults(userStage.getGameCode(data.getUserId()));
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.RESULTS, resultsCurrentRound);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = RESULTS_ENDPOINT;
					break;
				}
				case UserStageMonitor.WAITING_ROOM_SEE_RESULTS: {
					int roundsLeft = scrouchLogic.roundsLeft(userStage.getGameCode(data.getUserId()));
					int sessionsLeft = scrouchLogic.sessionsLeft(userStage.getGameCode(data.getUserId()));
					Application.logger
							.info("user " + data.getUserId() + " inserted waiting for all to see scores handler ");
					Application.logger.info("rounds left: " + roundsLeft + ", sessions left " + sessionsLeft);
					if (roundsLeft > 0) {
						Application.logger.info("user " + data.getUserId() + " in more rounds left ");
						DrawingTrueSentencePair drawingSentence = scrouchLogic
								.getCurrentDrawingSentencePlayer(userStage.getGameCode(data.getUserId()));
						HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.FALSING, drawingSentence);
						Application.logger.info(htmlData);
						userStage.setStageData(data.getUserId(), htmlData);
						urlRedirection = FALSING_ENDPOINT;
						if (drawingSentence.getPlayerId() == data.getUserId()) {
							urlRedirection = WAITING_ROOM_ENDPOINT;
						}
//						response = objectMapper.writeValueAsBytes(new FrontendResponse(true, FALSING_ENDPOINT));
						break;
					} else if (sessionsLeft > 0) {
						Application.logger.info("user " + data.getUserId() + " in more sessions left ");
						String trueSentence = scrouchLogic.getNextTrueSentence(userStage.getGameCode(data.getUserId()));
						HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.DRAWING, trueSentence);
						Application.logger.info(htmlData);
						userStage.setStageData(data.getUserId(), htmlData);
						urlRedirection = DRAWING_ENDPOINT;
//						response = objectMapper.writeValueAsBytes(new FrontendResponse(true, DRAWING_ENDPOINT));
						break;
					}
					Application.logger.info("user " + data.getUserId() + " in no sessions left - to winner ");
					PlayerPersonalInfo winner = scrouchLogic.getWinner(userStage.getGameCode(data.getUserId()));
					Application.logger.info("winner = " + winner.getId());
					HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WINNER, winner);
					Application.logger.info(htmlData);
					userStage.setStageData(data.getUserId(), htmlData);
					urlRedirection = WINNER_ENDPOINT;
//					response = objectMapper.writeValueAsBytes(new FrontendResponse(true, WINNER_ENDPOINT));
					break;
				}
				case UserStageMonitor.WAITING_ROOM_DECIDING: {
					Application.logger.info(data.getUserId() + " on waiting for deciding handler");
					boolean isAnotherGame = scrouchLogic.isAnotherGame(userStage.getGameCode(data.getUserId()));
					if (isAnotherGame) {
						String trueSentence = scrouchLogic.getNextTrueSentence(userStage.getGameCode(data.getUserId()));
						HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.DRAWING, trueSentence);
						Application.logger.info(htmlData);
						userStage.setStageData(data.getUserId(), htmlData);
						urlRedirection = DRAWING_ENDPOINT;
					} else {
						HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.NOT_ENOUGH_PLAYERS);
						Application.logger.info(htmlData);
						userStage.setStageData(data.getUserId(), htmlData);
						urlRedirection = NOT_ENOUGH_PLAYERS_ENDPOINT;
					}
					break;
				}
				default:
					break;
				}
			}
			response = objectMapper.writeValueAsBytes(new FrontendResponse(isContinue, urlRedirection));

			break;

		}
		case PERSONAL_INFO_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " ineserted personal info ");
			BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(data.getProfil()));
//			String profilPath = ConfigServer.getProperty(ConfigServer.IMAGES_PROFIL_BASE_DIR) + data.getUserId()
//					+ ConfigServer.getProperty(ConfigServer.PROFIL_POSTFIX) + "."
//					+ ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE);
//			ImageIO.write(bImage, ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE),
//					new File(ConfigServer.getProperty(ConfigServer.ASSETS_BASE_DIR) + profilPath));
			int playersLeft = scrouchLogic.registerPlayerInformation(data.getUserId(), data.getName(), bImage);
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_REGISTERING_INFO);
			Application.logger.info(htmlData + ". left " + playersLeft + " players");
			userStage.setStageData(data.getUserId(), htmlData);
			if (playersLeft == 0) {
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			response = objectMapper.writeValueAsBytes("");

			break;
		}
		case DRAWING_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " ineserted drawing page ");
			BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(data.getDrawing()));
//			String drawingPath = ConfigServer.getProperty(ConfigServer.IMAGES_DRAWING_BASE_DIR) + data.getUserId()
//					+ ConfigServer.getProperty(ConfigServer.DRAWING_POSTFIX) + "."
//					+ ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE);
//			ImageIO.write(bImage, ConfigServer.getProperty(ConfigServer.IMAGE_FILE_TYPE),
//					new File(ConfigServer.getProperty(ConfigServer.ASSETS_BASE_DIR) + drawingPath));
			int playersLeft = scrouchLogic.addPlayerDrawing(data.getUserId(), bImage, data.getTrueSentence());
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_DRAWING);
			Application.logger.info(htmlData + ". left " + playersLeft + " players");
			userStage.setStageData(data.getUserId(), htmlData);
			if (playersLeft == 0) {
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			response = objectMapper.writeValueAsBytes("");

			break;
		}
		case FALSING_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " inesrted falsing handler ");
			int playersLeft = scrouchLogic.addPlayerFalseDiscriptionToCurrentDrawing(data.getUserId(),
					data.getFalseSentence());
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_FALSING);
			Application.logger.info(htmlData + ". left " + playersLeft + " players");
			userStage.setStageData(data.getUserId(), htmlData);
			if (playersLeft == 0) {
				userStage.resetBarriersToClosed(userStage.getGameCode(data.getUserId())); // first point to reset barriers
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			response = objectMapper.writeValueAsBytes("");

			break;
		}
		case GUESSING_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " inserted guessing handler ");
			int playersLeft = scrouchLogic.addPlayerGuessToCurrentDrawing(data.getUserId(), data.getGuessSentence());
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_GUESSING);
			Application.logger.info(htmlData + ". left " + playersLeft + " players");
			userStage.setStageData(data.getUserId(), htmlData);
			if (playersLeft == 0) {
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			response = objectMapper.writeValueAsBytes("");

			break;
		}
		case RESULTS_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " inserted results page ");
			Map<PlayerPersonalInfo, Integer> scoreBoardLastRound = scrouchLogic
					.getLastRoundScoreBoard(userStage.getGameCode(data.getUserId()));
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.LAST_ROUND_SCORES, scoreBoardLastRound);
			Application.logger.info(htmlData);
			userStage.setStageData(data.getUserId(), htmlData);
			response = objectMapper.writeValueAsBytes(new FrontendResponse(true, SCORES_ENDPOINT));

			break;
		}
		case SCORES_ENDPOINT: {
			Map<PlayerPersonalInfo, Integer> scoreBoardTotal = scrouchLogic
					.getTotalScoreBoard(userStage.getGameCode(data.getUserId()));
			Application.logger.info("user " + data.getUserId() + " inserted scores handler");
			switch (userStage.getStage(data.getUserId())) {
			case UserStageMonitor.LAST_ROUND_SCORES: {
				HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.TOTAL_SCORES, scoreBoardTotal);
				Application.logger.info(htmlData);
				userStage.setStageData(data.getUserId(), htmlData);
				response = objectMapper.writeValueAsBytes(new FrontendResponse(true, SCORES_ENDPOINT));
				break;
			}
			case UserStageMonitor.TOTAL_SCORES: {
				Application.logger.info("user " + data.getUserId() + " inserted total scores handler ");
				int playersLeft = scrouchLogic.addPlayerSawScores(data.getUserId());
				HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_SEE_RESULTS);
				Application.logger.info(htmlData + ". left " + playersLeft + " players");
				userStage.setStageData(data.getUserId(), htmlData);
				if (playersLeft == 0) {
					userStage.resetBarriersToClosed(userStage.getGameCode(data.getUserId()));
					userStage.setUpFromWaitingRoom(data.getUserId());
				}
				response = objectMapper.writeValueAsBytes(new FrontendResponse(true, WAITING_ROOM_ENDPOINT));
			}
			}
			break;
		}
		case WINNER_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " inserted winner page ");
			HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.ANOTHER_GAME);
			Application.logger.info(htmlData);
			userStage.setStageData(data.getUserId(), htmlData);
			response = objectMapper.writeValueAsBytes(new FrontendResponse(true, ANOTHER_GAME_ENDPOINT));

			break;
		}
		case ANOTHER_GAME_ENDPOINT: {
			Application.logger.info("user " + data.getUserId() + " inserted another game page ");
			boolean decision = data.getDecision();
			int playersLeft = scrouchLogic.addPlayerContinueChoice(data.getUserId(), decision);
			if (decision) {
				HtmlData htmlData = new HtmlData(data.getUserId(), UserStageMonitor.WAITING_ROOM_DECIDING);
				Application.logger.info(htmlData);
				userStage.setStageData(data.getUserId(), htmlData);
				response = objectMapper.writeValueAsBytes(new FrontendResponse(true, WAITING_ROOM_ENDPOINT));

			} else {
				response = objectMapper.writeValueAsBytes(new FrontendResponse(true, UserStageMonitor.HOME_SCREEN));
			}
			if (playersLeft == 0) {
				userStage.setUpFromWaitingRoom(data.getUserId());
			}
			if (!decision) {
				userStage.removePlayer(data.getUserId());
			}

			break;
		}

		default:
			break;
		}

		WebServer.addContentType(asset, exchange);

		WebServer.sendResponse(response, exchange);
	}
}
