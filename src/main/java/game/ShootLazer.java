package game;

import skyport.api.SkyportClient;
import skyport.api.game.Direction;
import skyport.api.game.WeaponType;

public class ShootLazer implements Action {

	private Direction d;
	
	public ShootLazer(WeaponType w, Direction d) {
		super();
		this.d = d;
	}

	public void perform(SkyportClient client) {
		client.fireLaser(d);
	}

}
