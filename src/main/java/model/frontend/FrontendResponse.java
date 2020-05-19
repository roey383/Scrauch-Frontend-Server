package model.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendResponse {

	private boolean isContinue;
	private boolean alreadyParticipating;
	private boolean redirectingLast;
	private String urlRedirection;

	public FrontendResponse(boolean isContinue, String urlRedirection) {
		super();
		this.isContinue = isContinue;
		this.urlRedirection = urlRedirection;
		this.alreadyParticipating = false;
		this.redirectingLast = false;
	}

	public FrontendResponse(boolean isContinue, boolean alreadyParticipating, String urlRedirection) {
		super();
		this.isContinue = isContinue;
		this.alreadyParticipating = alreadyParticipating;
		this.urlRedirection = urlRedirection;
		this.redirectingLast = false;
	}

	public FrontendResponse(boolean isContinue, boolean alreadyParticipating, boolean redirectingLast,
			String urlRedirection) {
		super();
		this.isContinue = isContinue;
		this.alreadyParticipating = alreadyParticipating;
		this.redirectingLast = redirectingLast;
		this.urlRedirection = urlRedirection;
	}

	@JsonProperty(value="is_continue")
	public boolean isContinue() {
		return isContinue;
	}
	
	@JsonProperty(value="already_participating")
	public boolean getAlreadyParticipating() {
		return alreadyParticipating;
	}

	public String getUrlRedirection() {
		return urlRedirection;
	}

	@JsonProperty(value="redirecting_last")
	public boolean getRedirectingLast() {
		return redirectingLast;
	}

	
}
