package htmlAccessories;

public class UserStage {
	
	protected String stage = null;
	protected Long userId = null;
	protected boolean isContinue;

	public UserStage(long userId, String stage) {
		// TODO Auto-generated constructor stub
		this.userId = userId;
		this.stage = stage;
		this.isContinue = false;
	}

}
