package game;

import skyport.api.SkyportClient;

public class UpgradeMortarz implements Action {

	public void perform(SkyportClient client) {
		client.upgrade("mortar");
	}

}
