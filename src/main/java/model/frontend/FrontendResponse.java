package model.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendResponse {

	private boolean isContinue;
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

	public String getUrlRedirection() {
		return urlRedirection;
	}
	
	
}
