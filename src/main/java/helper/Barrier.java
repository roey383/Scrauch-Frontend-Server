package helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import controller.UserStageMonitor;

public class Barrier {

	Map<String, Gate> stageToIsGateOpen;
	boolean oddRound;
	boolean anotherGameOdd;
	boolean extraGame;

	public Barrier() {
		this.stageToIsGateOpen = new ConcurrentHashMap<String, Gate>();
		this.oddRound = true;
		this.anotherGameOdd = true;
		this.extraGame = false;

		for (String stage : UserStageMonitor.getWaitingRoomsStages()) {
			stageToIsGateOpen.put(stage, new Gate(false));
		}

		stageToIsGateOpen.put(UserStageMonitor.WAITING_PLAYERS_PRESENTATION, new Gate(true));
	}

	public synchronized boolean isGateOpen(String stage) {
		return stage.equals(UserStageMonitor.WAITING_ROOM_DECIDING)
				? stageToIsGateOpen.get(stage).isGateOpen(anotherGameOdd)
				: stageToIsGateOpen.get(stage).isGateOpen(oddRound);
	}

	public synchronized void setStageGateOpen(String stage) {
		// TODO Auto-generated method stub

		Gate gate = new Gate(oddRound, !oddRound);

		stageToIsGateOpen.put(stage, gate);

		switch (stage) {
		case UserStageMonitor.WAITING_ROOM_DRAWING:
			if (extraGame) {
				anotherGameOdd = !anotherGameOdd;
			}
		case UserStageMonitor.WAITING_ROOM_SEE_RESULTS: {
			stageToIsGateOpen.put(stage, new Gate(true));
			oddRound = !oddRound;

			break;
		}
		case UserStageMonitor.WAITING_ROOM_FALSING: {
			stageToIsGateOpen.put(UserStageMonitor.WAITING_ROOM_SEE_RESULTS, new Gate(!oddRound, oddRound));

			break;
		}
		case UserStageMonitor.WAITING_ROOM_DECIDING: {
			extraGame = true;
			stageToIsGateOpen.put(stage, new Gate(anotherGameOdd, !anotherGameOdd));

			break;
		}

		default:
			break;
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

			return oddRound ? gateOdd : gatePair;
		}

	}

}
