package game;

import skyport.api.SkyportClient;

public class UpgradeLazerz implements Action {

	public void perform(SkyportClient client) {
		client.upgrade("laser");
	}

}
