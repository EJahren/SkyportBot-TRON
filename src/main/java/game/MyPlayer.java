package game;

import java.util.ArrayList;
import java.util.List;

import skyport.api.SkyportClient;
import skyport.api.game.Direction;
import skyport.api.game.GameState;
import skyport.api.game.Player;
import skyport.api.game.Point;
import skyport.api.game.WeaponType;

public class MyPlayer implements Runnable {

	/**
	 * The client connection to the skyport server.
	 */
	private SkyportClient client;

	/**
	 * The name of the player.
	 */
	private String name;

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

		} while (state != null);
	}

}
