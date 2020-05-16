package htmlAccessories;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import controller.UserStageMonitor;
import logic.DrawingTrueSentencePair;
import logic.PlayerPersonalInfo;
import logic.Result;

public class HtmlData extends UserStage {

	private Integer numOfPlayers = null;
	private Integer numOfSessions = null;
	private String gameCode = null;
	private String name = null;
	private String profilPath = null;
	private String drawingPath = null;
	private String trueSentence = null;
	private String falseSentence = null;
	private String guessSentence = null;
	private List<PlayerPersonalInfo> playersInfo = null;
	private DrawingTrueSentencePair drawingSentence = null;
	private List<String> allSentences = null;
	private List<Result> results = null;
	private Map<PlayerPersonalInfo, Integer> scoreBoard = null;
	private PlayerPersonalInfo winner = null;

	public Integer getNumOfPlayers() {
		return numOfPlayers;
	}

	public Integer getNumOfSessions() {
		return numOfSessions;
	}

	public String getGameCode() {
		return gameCode;
	}

	public String getName() {
		return name;
	}

	public String getProfilPath() {
		return profilPath;
	}

	public String getDrawingPath() {
		return drawingPath;
	}

	public String getTrueSentence() {
		return trueSentence;
	}

	public String getFalseSentence() {
		return falseSentence;
	}

	public String getGuessSentence() {
		return guessSentence;
	}

	public List<PlayerPersonalInfo> getPlayersInfo() {
		return playersInfo;
	}

	public DrawingTrueSentencePair getDrawingSentence() {
		return drawingSentence;
	}

	public List<String> getAllSentences() {
		return allSentences;
	}

	public List<Result> getResults() {
		return results;
	}

	public Map<PlayerPersonalInfo, Integer> getScoreBoard() {
		return scoreBoard;
	}

	public PlayerPersonalInfo getWinner() {
		return winner;
	}


	public HtmlData(long userId, String stage) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
	}

	public HtmlData(long userId, String stage, String stringData) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		switch (stage) {
		case UserStageMonitor.WAITING_ROOM_JOINERS:
			this.gameCode = stringData;
			break;
		case UserStageMonitor.DRAWING:
			this.trueSentence = stringData;
			break;

		default:
			break;
		}

	}

	public HtmlData(long userId, String stage, String name, String profilPath) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		switch (stage) {
		case UserStageMonitor.WAITING_ROOM_REGISTERING_INFO:
			this.name = name;
			this.profilPath = profilPath;
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> HtmlData(long userId, String stage, List<T> list) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		switch (stage) {
		case UserStageMonitor.WAITING_PLAYERS_PRESENTATION:
			this.playersInfo = (List<PlayerPersonalInfo>) list;
			break;
		case UserStageMonitor.RESULTS:
			this.results = (List<Result>) list;
			break;
		default:
			break;
		}
	}

	public <T> HtmlData(long userId, String stage, List<String> allSentences, DrawingTrueSentencePair drawingSentence) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		this.allSentences = allSentences;
		this.drawingSentence = drawingSentence;
	}

	public HtmlData(long userId, String stage, DrawingTrueSentencePair drawingSentence) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		this.drawingSentence = drawingSentence;
	}

	public HtmlData(long userId, String stage, Map<PlayerPersonalInfo, Integer> scoreBoard) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		this.scoreBoard = scoreBoard;
	}

	public HtmlData(long userId, String stage, PlayerPersonalInfo winner) {
		// TODO Auto-generated constructor stub
		super(userId, stage);
		this.winner = winner;
	}

	public String getStage() {
		// TODO Auto-generated method stub
		return stage;
	}

	@Override
	public String toString() {

		StringBuilder str = new StringBuilder();
		
		str.append("HtmlData: userId = " + this.userId + ", stage = " + this.stage + ", ");

		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				Object val = f.get(this);
				if (val != null && !f.toString().contains("Lock")) {
					String[] tokens = f.toString().split("\\.");
					str.append(tokens[tokens.length-1] + " = " + val + ", ");
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		str.delete(str.length()-2, str.length()-1);

		return str.toString();
	}

	public long getUserId() {
		// TODO Auto-generated method stub
		return this.userId;
	}

	public void setStage(String stage) {
		// TODO Auto-generated method stub
		this.stage = stage;
	}

}
