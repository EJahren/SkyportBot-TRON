package game;

import skyport.api.SkyportClient;
import skyport.api.game.GameState;
import skyport.api.game.Map;
import skyport.api.game.Player;

public class MyPlayer implements Runnable {

	/**
	 * The client connection to the skyport server.
	 */
	private SkyportClient client;
	
	private Player me;
	
	private int explosium;
	private int rubidium;
	private int scrap;
	
	/**
	 * The name of the player.
	 */
	private String name;

	private Map map;

	public MyPlayer(String name, String host, int port) {
		this.name = name;

		// Creates the connection to the server
		this.client = new SkyportClient(host, port);
		this.client.connect();
		this.client.sendHandshake(this.name);

		// Requests the chosen weapons
		this.client.sendLoadout("mortar", "laser");
	}

	public void run() {
		GameState state;
		do {
			// Generate the game state for the current turn
			state = this.client.nextTurn(this.name);
			me = state.getPlayers().get(0);
			map = state.getMap();
			
			

		} while (state != null);
	}

}
