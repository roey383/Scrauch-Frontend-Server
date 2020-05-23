package controller;

import app.Application;
import htmlAccessories.HtmlData;

public class User {

	private Game game;
	private HtmlData htmlData;
	private Long id;

	public User(Long id, Game game) {
		// TODO Auto-generated constructor stub
		this.game = game;
		this.id = id;
	}

	public void setHtmlData(HtmlData htmlData) {
		// TODO Auto-generated method stub
		this.htmlData = htmlData;
//		this.stage = htmlData.getStage();
		
	}

	public void setStageOpen() {
		// TODO Auto-generated method stub
		this.game.setStageOpen(htmlData.getStage());
		
	}

	public String getStage() {
		// TODO Auto-generated method stub
		return htmlData.getStage();
	}

	public boolean isCurrentStageGateOpen() {
		// TODO Auto-generated method stub
		Application.logger.info("in is current gate open: user = " + this);
		return this.game.isGateOpen(htmlData.getStage());
	}

	public String getGameCode() {
		// TODO Auto-generated method stub
		return this.game.getCode();
	}

	public void removeSelf() {
		// TODO Auto-generated method stub
		this.game.removePlayer(this);
		
	}

	public HtmlData getHtmlData() {
		// TODO Auto-generated method stub
		return this.htmlData;
	}

	@Override
	public String toString() {
		return "User [game=" + game + ", htmlData=" + htmlData + ", id=" + id + "]";
	}

	
}
