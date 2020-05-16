package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import app.Application;
import helper.Barrier;
import htmlAccessories.HtmlData;
import networking.DataHandlers;

public class UserStageMonitor {

	public static final String WAITING_ROOM_JOINERS = "waiting_room_joiners";
	public static final String WAITING_ROOM_REGISTERING_INFO = "waiting_room_registering_info";
	public static final String PERSONAL_INFO = "personal_info";
	public static final String WAITING_PLAYERS_PRESENTATION = "players_presentation";
//	public static final String WAITING_ROOM_PLAYERS_PRESENTATION = "waiting_room_players_presentation";
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

	private Map<Long, String> playerIdToGameCode;
	private Map<String, List<Long>> gameCodeToPlayersIds;
	private Map<Long, HtmlData> playerIdToHtmlData;
	private Map<String, Barrier> gameCodeToContinueNextStageFlag;
	private Map<String, String> stageToEndPoint;

	public UserStageMonitor() {
		super();
		this.playerIdToGameCode = new ConcurrentHashMap<Long, String>();
		this.playerIdToHtmlData = new ConcurrentHashMap<Long, HtmlData>();
		this.gameCodeToPlayersIds = new ConcurrentHashMap<String, List<Long>>();
		this.gameCodeToContinueNextStageFlag = new ConcurrentHashMap<String, Barrier>();
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
//		waitingStages.add(WAITING_ROOM_PLAYERS_PRESENTATION);
		waitingStages.add(WAITING_ROOM_DRAWING);
		waitingStages.add(WAITING_ROOM_FALSING);
		waitingStages.add(WAITING_ROOM_GUESSING);
		waitingStages.add(WAITING_ROOM_SEE_RESULTS);
		waitingStages.add(WAITING_ROOM_DECIDING);
		return waitingStages;
	}

	public void initBarriers(String gameCode) {
		// TODO Auto-generated method stub
		gameCodeToContinueNextStageFlag.put(gameCode, new Barrier());

	}

	public synchronized void addUser(long userId, String gameCode) {
		// TODO Auto-generated method stub
		playerIdToGameCode.put(userId, gameCode);
		if (!gameCodeToPlayersIds.containsKey(gameCode)) {
			gameCodeToPlayersIds.put(gameCode, new ArrayList<Long>());
		}
		gameCodeToPlayersIds.get(gameCode).add(userId);
	}

	public void setStageData(long userId, HtmlData htmlData) {
		// TODO Auto-generated method stub
		playerIdToHtmlData.put(userId, htmlData);

		String gameCode = playerIdToGameCode.get(userId);
		if (waitingStages.contains(htmlData.getStage())
				&& gameCodeToContinueNextStageFlag.get(gameCode).isGateOpen(htmlData.getStage())) {
			htmlData.setContinueNextStage();
		}
	}

	public synchronized void setUpFromWaitingRoom(long userId) {
		// TODO Auto-generated method stub
		String gameCode = playerIdToGameCode.get(userId);
		String stage = playerIdToHtmlData.get(userId).getStage();
		gameCodeToContinueNextStageFlag.get(gameCode).setStageGateOpen(stage);

//		for (Long playerId : gameCodeToPlayersIds.get(gameCode)) {
//			if (playerIdToHtmlData.containsKey(playerId) && playerIdToHtmlData.get(playerId).getStage().equals(stage)) {
//				try {
//					playerIdToHtmlData.get(playerId).setContinueNextStage();
//				} catch (Exception e) {
//					// TODO: handle exception
//					Application.logger.error("error set up from waiting: " + e);
//				}
//			}
//		}

	}

	public boolean getUserContinueNextStage(long userId) {
		// TODO Auto-generated method stub
//		return playerIdToHtmlData.get(userId).getIsContinueNextStage();
		boolean isOpen = gameCodeToContinueNextStageFlag.get(playerIdToGameCode.get(userId)).isGateOpen(playerIdToHtmlData.get(userId).getStage());
		Application.logger.info("GATE: " + userId + ", stage: " + playerIdToHtmlData.get(userId).getStage() + ", gate: " + isOpen);
		return isOpen;
	}

	public static Set<String> getWaitingRoomsStages() {
		// TODO Auto-generated method stub
		return waitingStages;
	}

	public String getStage(long userId) {
		// TODO Auto-generated method stub
		return playerIdToHtmlData.get(userId).getStage();
	}

	public String getGameCode(long userId) {
		// TODO Auto-generated method stub
		return playerIdToGameCode.get(userId);
	}

	public void removePlayer(long userId) {
		// TODO Auto-generated method stub
		Application.logger.warn("removing playuer " + userId);
//		this.playerIdToHtmlData.remove(userId);
		String gameCode = playerIdToGameCode.get(userId);
		List<Long> playersIds = new ArrayList<Long>();
		for (Long playerId : this.gameCodeToPlayersIds.get(gameCode)) {
			if (playerId != userId) {
				playersIds.add(playerId);
			}
		}
		this.gameCodeToPlayersIds.put(gameCode, playersIds);
		playerIdToGameCode.remove(userId);

	}

	public HtmlData getHtmlData(long userId) {
		// TODO Auto-generated method stub
		return playerIdToHtmlData.get(userId);
	}

	public boolean userExits(long userId) {
		// TODO Auto-generated method stub
		return playerIdToGameCode.containsKey(userId);
	}

	public String getEndPointForCurrentStage(long userId) {
		// TODO Auto-generated method stub
		if (!userExits(userId)) {
			return HOME_SCREEN;
		}
		String stage = playerIdToHtmlData.get(userId).getStage();
		if (stage.equals(FALSING) && playerIdToHtmlData.get(userId).getDrawingSentence().getPlayerId() == userId) {
			return DataHandlers.WAITING_ROOM_ENDPOINT;
		}
		return stageToEndPoint.get(stage);
	}

}
