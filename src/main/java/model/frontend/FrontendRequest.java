package model.frontend;

public class FrontendRequest {
    
	private String url;
	private long userId;
	private int numOfPlayers;
	private int numOfSessions;
	private String gameCode;
	private String name;
	private byte[] profil;
	private byte[] drawing;
	private String trueSentence;
	private String falseSentence;
	private String guessSentence;
	private boolean decision;
	
	public long getUserId() {
		return userId;
	}
	public String getGameCode() {
		return gameCode;
	}
	public String getName() {
		return name;
	}
	public byte[] getProfil() {
		return profil;
	}
	public byte[] getDrawing() {
		return drawing;
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
	public String getUrl() {
		return url;
	}
	public int getNumOfPlayers() {
		return numOfPlayers;
	}
	public int getNumOfSessions() {
		return numOfSessions;
	}
	public boolean getDecision() {
		return decision;
	}


}
