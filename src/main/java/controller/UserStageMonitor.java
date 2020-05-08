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

public class UserStageMonitor {

	public static final String WAITING_ROOM_JOINERS = "waiting_room_joiners";
	public static final String WAITING_ROOM_REGISTERING_INFO = "waiting_room_registering_info";
	public static final String PERSONAL_INFO = "personal_info";
	public static final String PLAYERS_PRESENTATION = "players_presentation";
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

	public UserStageMonitor() {
		super();
		this.playerIdToGameCode = new ConcurrentHashMap<Long, String>();
		this.playerIdToHtmlData = new ConcurrentHashMap<Long, HtmlData>();
		this.gameCodeToPlayersIds = new ConcurrentHashMap<String, List<Long>>();
		this.gameCodeToContinueNextStageFlag = new ConcurrentHashMap<String, Barrier>();
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
		waitingStages.add(WAITING_ROOM_DECIDING);
		waitingStages.add(WAITING_ROOM_SEE_RESULTS);
		return waitingStages;
	}

	public void resetBarriersToClosed(String gameCode) {
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

		for (Long playerId : gameCodeToPlayersIds.get(gameCode)) {
			if (playerIdToHtmlData.containsKey(playerId) && playerIdToHtmlData.get(playerId).getStage().equals(stage)) {
				playerIdToHtmlData.get(playerId).setContinueNextStage();
			}
		}

	}

	public boolean getUserContinueNextStage(long userId) {
		// TODO Auto-generated method stub
//		String gameCode = playerIdToGameCode.get(userId);
//		if (gameCodeToContinueNextStageFlag.get(gameCode) == true) {
//			return true;
//		}

		return playerIdToHtmlData.get(userId).getIsContinueNextStage();
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
		Application.logger.warn("remove plaer - NOT IMPLEMENTED YET");
	}

	public HtmlData getHtmlData(long userId) {
		// TODO Auto-generated method stub
		return playerIdToHtmlData.get(userId);
	}

}
