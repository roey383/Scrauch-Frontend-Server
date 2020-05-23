package controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import app.Application;
import htmlAccessories.HtmlData;
import networking.DataHandlers;

public class UserStageMonitor {

	public static final String WAITING_ROOM_JOINERS = "waiting_room_joiners";
	public static final String WAITING_ROOM_REGISTERING_INFO = "waiting_room_registering_info";
	public static final String PERSONAL_INFO = "personal_info";
	public static final String WAITING_PLAYERS_PRESENTATION = "players_presentation";
	public static final String DRAWING = "drawing_stage";
	public static final String WAITING_ROOM_DRAWING = "waiting_room_drawing";
	public static final String FALSING = "falsing_stage";
	public static final String WAITING_ROOM_FALSING = "waiting_room_falsing";
	public static final String GUESSING = "guessing";
	public static final String WAITING_ROOM_GUESSING = "waiting_room_guessing";
	public static final String RESULTS = "results_stage";
	public static final String WAITING_ROOM_DECIDING = "waiting_room_deciding";
	public static final String NOT_ENOUGH_PLAYERS = "not_enough_players_stage";
	public static final String LAST_ROUND_SCORES = "last_round_scores_stage";
	public static final String TOTAL_SCORES = "total_scores_stage";
	public static final String WINNER = "winner_stage";
	public static final String ANOTHER_GAME = "another_game_stage";
	public static final String HOME_SCREEN = "/";
	public static final String WAITING_ROOM_SEE_RESULTS = "waiting_room_see_results";

	public static Set<String> waitingStages = initWaitingStages();

	private Map<Long, User> userIdToUser;
	private Map<String, Game> codeToGame;
	private Map<String, String> stageToEndPoint;

	public UserStageMonitor() {
		super();
		this.userIdToUser = new ConcurrentHashMap<Long, User>();
		this.codeToGame = new ConcurrentHashMap<String, Game>();
		this.stageToEndPoint = buildStageToEndpoint();
	}

	private Map<String, String> buildStageToEndpoint() {
		// TODO Auto-generated method stub
		Map<String, String> stageToEndPoint = new ConcurrentHashMap<String, String>();
		stageToEndPoint.put(WAITING_ROOM_JOINERS, DataHandlers.WAITING_ROOM_ENDPOINT);
		stageToEndPoint.put(WAITING_ROOM_REGISTERING_INFO, DataHandlers.WAITING_ROOM_ENDPOINT);
		stageToEndPoint.put(PERSONAL_INFO, DataHandlers.PERSONAL_INFO_ENDPOINT);
		stageToEndPoint.put(WAITING_PLAYERS_PRESENTATION, DataHandlers.PLAYERS_PRESENTATION_ENDPOINT);
		stageToEndPoint.put(DRAWING, DataHandlers.DRAWING_ENDPOINT);
		stageToEndPoint.put(WAITING_ROOM_DRAWING, DataHandlers.WAITING_ROOM_ENDPOINT);
		stageToEndPoint.put(FALSING, DataHandlers.FALSING_ENDPOINT);
		stageToEndPoint.put(WAITING_ROOM_FALSING, DataHandlers.WAITING_ROOM_ENDPOINT);
		stageToEndPoint.put(GUESSING, DataHandlers.GUESSING_ENDPOINT);
		stageToEndPoint.put(WAITING_ROOM_GUESSING, DataHandlers.WAITING_ROOM_ENDPOINT);
		stageToEndPoint.put(RESULTS, DataHandlers.RESULTS_ENDPOINT);
		stageToEndPoint.put(WAITING_ROOM_DECIDING, DataHandlers.WAITING_ROOM_ENDPOINT);
		stageToEndPoint.put(NOT_ENOUGH_PLAYERS, DataHandlers.NOT_ENOUGH_PLAYERS_ENDPOINT);
		stageToEndPoint.put(LAST_ROUND_SCORES, DataHandlers.SCORES_ENDPOINT);
		stageToEndPoint.put(TOTAL_SCORES, DataHandlers.SCORES_ENDPOINT);
		stageToEndPoint.put(WINNER, DataHandlers.WINNER_ENDPOINT);
		stageToEndPoint.put(ANOTHER_GAME, DataHandlers.ANOTHER_GAME_ENDPOINT);
		stageToEndPoint.put(HOME_SCREEN, HOME_SCREEN);
		stageToEndPoint.put(WAITING_ROOM_SEE_RESULTS, DataHandlers.WAITING_ROOM_ENDPOINT);

		return stageToEndPoint;
	}

	private static HashSet<String> initWaitingStages() {
		// TODO Auto-generated method stub
		HashSet<String> waitingStages = new HashSet<String>();
		waitingStages.add(WAITING_ROOM_JOINERS);
		waitingStages.add(WAITING_ROOM_REGISTERING_INFO);
		waitingStages.add(WAITING_ROOM_DRAWING);
		waitingStages.add(WAITING_ROOM_FALSING);
		waitingStages.add(WAITING_ROOM_GUESSING);
		waitingStages.add(WAITING_ROOM_SEE_RESULTS);
		waitingStages.add(WAITING_ROOM_DECIDING);
		return waitingStages;
	}

	public synchronized void addUser(long userId, String gameCode) {
		// TODO Auto-generated method stub
		Game game;
		if (!codeToGame.containsKey(gameCode)) {
			game = new Game(gameCode);
			codeToGame.put(gameCode, game);
		}
		game = codeToGame.get(gameCode);
		User user = new User(userId, game);
		userIdToUser.put(userId, user);
		game.addUser(user);
	}

	public void setStageData(long userId, HtmlData htmlData) {
		// TODO Auto-generated method stub
		userIdToUser.get(userId).setHtmlData(htmlData);

	}

	public synchronized void setUpFromWaitingRoom(long userId) {
		// TODO Auto-generated method stub
		userIdToUser.get(userId).setStageOpen();

	}

	public boolean getUserContinueNextStage(long userId) {
		// TODO Auto-generated method stub
//		return playerIdToHtmlData.get(userId).getIsContinueNextStage();
		Application.logger.debug("GATE: " + userId + ", stage: " + userIdToUser.get(userId).getStage() + ", gate: "
				+ userIdToUser.get(userId).isCurrentStageGateOpen());

		return userIdToUser.get(userId).isCurrentStageGateOpen();
	}

	public static Set<String> getWaitingRoomsStages() {
		// TODO Auto-generated method stub
		return waitingStages;
	}

	public String getStage(long userId) {
		// TODO Auto-generated method stub
		return userIdToUser.get(userId).getStage();
	}

	public String getGameCode(long userId) {
		// TODO Auto-generated method stub
		return userIdToUser.get(userId).getGameCode();
	}

	public void removePlayer(long userId) {
		// TODO Auto-generated method stub
		Application.logger.warn("removing player " + userId);

		userIdToUser.get(userId).removeSelf();
		userIdToUser.remove(userId);

	}

	public HtmlData getHtmlData(long userId) {
		// TODO Auto-generated method stub
		return userIdToUser.get(userId).getHtmlData();
	}

	public boolean userExits(long userId) {
		// TODO Auto-generated method stub
		return userIdToUser.containsKey(userId);
	}

	public String getEndPointForCurrentStage(long userId) {
		// TODO Auto-generated method stub
		if (!userExits(userId)) {
			return HOME_SCREEN;
		}
		User user = userIdToUser.get(userId);
		String stage = user.getStage();
		if (stage.equals(FALSING) && user.getHtmlData().getDrawingSentence().getPlayerId() == userId) {
			return DataHandlers.WAITING_ROOM_ENDPOINT;
		}
		return stageToEndPoint.get(stage);
	}

	public boolean gameExits(String gameCode) {
		// TODO Auto-generated method stub
		return codeToGame.containsKey(gameCode);
	}

}
