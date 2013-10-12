package game;

import skyport.api.SkyportClient;
import skyport.api.game.Direction;

public class Move implements Action {

	private Direction dir;
	
	public Move(Direction dir) {
		super();
		this.dir = dir;
	}

	public void perform(SkyportClient client) {
		client.move(dir);
	}

}
