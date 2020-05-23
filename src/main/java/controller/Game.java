package controller;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import helper.Barrier;

public class Game {

	private Barrier barrier;
	private String code;
	private Set<User> users;

	public Game(String gameCode) {
		// TODO Auto-generated constructor stub
		this.code = gameCode;
		this.barrier = new Barrier();
		this.users = ConcurrentHashMap.newKeySet();
	}

	public void addUser(User user) {
		// TODO Auto-generated method stub
		this.users.add(user);
	}

	public void setStageOpen(String stage) {
		// TODO Auto-generated method stub
		this.barrier.setStageGateOpen(stage);
		
	}

	public boolean isGateOpen(String stage) {
		// TODO Auto-generated method stub
		return this.barrier.isGateOpen(stage);
	}

	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

	public void removePlayer(User user) {
		// TODO Auto-generated method stub
		users.remove(user);
		
	}

	@Override
	public String toString() {
		return "Game [code=" + code + "]";
	}

	
}
