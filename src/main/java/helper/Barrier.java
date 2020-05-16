package helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import controller.UserStageMonitor;

public class Barrier {

	Map<String, Gate> stageToIsGateOpen;
	boolean oddRound;

	public Barrier() {
		this.stageToIsGateOpen = new ConcurrentHashMap<String, Gate>();
		this.oddRound = true;

		for (String stage : UserStageMonitor.getWaitingRoomsStages()) {
			stageToIsGateOpen.put(stage, new Gate(false));
		}

		stageToIsGateOpen.put(UserStageMonitor.WAITING_PLAYERS_PRESENTATION, new Gate(true));
	}

	public boolean isGateOpen(String stage) {
		return stageToIsGateOpen.get(stage).isGateOpen(oddRound);
	}

	public synchronized void setStageGateOpen(String stage) {
		// TODO Auto-generated method stub
		
		Gate gate = new Gate(oddRound, !oddRound);
		
		stageToIsGateOpen.put(stage, gate);
		
		if (stage.equals(UserStageMonitor.WAITING_ROOM_SEE_RESULTS)) {
			stageToIsGateOpen.put(stage, new Gate(true));
			oddRound = !oddRound;
		}
		else if (stage.equals(UserStageMonitor.WAITING_ROOM_FALSING)) {
			stageToIsGateOpen.put(UserStageMonitor.WAITING_ROOM_SEE_RESULTS, new Gate(!oddRound, oddRound));
			
		}

	}

	public class Gate {

		boolean gateOdd;
		boolean gatePair;

		public Gate(boolean gate) {
			super();
			this.gateOdd = gate;
			this.gatePair = gate;
		}
		
		public Gate(boolean gateOdd, boolean gatePair) {
			super();
			this.gateOdd = gateOdd;
			this.gatePair = gatePair;
		}

		public synchronized boolean isGateOpen(boolean oddRound) {
			
			return oddRound ? gateOdd: gatePair;
		}
		
		
	}

}
