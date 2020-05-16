package model.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendResponse {

	private boolean isContinue;
	private boolean okNewGame;
	private String urlRedirection;

	public FrontendResponse(boolean isContinue, String urlRedirection) {
		super();
		this.isContinue = isContinue;
		this.urlRedirection = urlRedirection;
	}

	@JsonProperty(value="is_continue")
	public boolean isContinue() {
		return isContinue;
	}
	
	@JsonProperty(value="ok_new_game")
	public boolean okNewGame() {
		return okNewGame;
	}

	public String getUrlRedirection() {
		return urlRedirection;
	}
	
	
}
