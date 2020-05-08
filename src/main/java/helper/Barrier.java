package helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import controller.UserStageMonitor;

public class Barrier {
	
	Map<String, Boolean> stageToIsGateOpen;	
	
	public Barrier() {
		this.stageToIsGateOpen = new ConcurrentHashMap<String, Boolean>();
		for (String stage : UserStageMonitor.getWaitingRoomsStages()) {
			stageToIsGateOpen.put(stage, false);
		}
	}

	public boolean isGateOpen(String stage) {
		return stageToIsGateOpen.get(stage);
	}

	public void setStageGateOpen(String stage) {
		// TODO Auto-generated method stub
		stageToIsGateOpen.put(stage, true);
		
	}
	
	
}
