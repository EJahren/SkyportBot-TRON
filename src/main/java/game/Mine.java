package game;

import skyport.api.SkyportClient;

public class Mine implements Action {

	public void perform(SkyportClient client) {
		client.mine();
	}

}
