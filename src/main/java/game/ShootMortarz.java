package game;

import skyport.api.SkyportClient;
import skyport.api.game.Point;
import skyport.api.game.WeaponType;

public class ShootMortarz implements Action {

	private Point p;

	public ShootMortarz(Point p) {
		this.p = p;
	}

	public void perform(SkyportClient client) {
		client.fireMortar(p.getJ(), p.getK());
	}

}
