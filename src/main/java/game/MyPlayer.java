package game;


import java.util.List;
import java.util.Random;


import skyport.api.SkyportClient;
import skyport.api.game.BFSIterator;
import skyport.api.game.Direction;
import skyport.api.game.GameState;

import skyport.api.game.Map;

import skyport.api.game.Player;
import skyport.api.game.Point;
import skyport.api.game.weapon.Weapon;
import skyport.api.game.Tile;
import skyport.api.game.weapon.Droid;
import skyport.api.game.weapon.Mortar;
import skyport.api.game.weapon.ShootActionIterator;

public class MyPlayer implements Runnable {

	/**
	 * The client connection to the skyport server.
	 */
	private SkyportClient client;
	
	private int explosium;
	@SuppressWarnings("unused")
	private int rubidium;
	private int scrap;
	private int actionsLeft;
	private GameState state;
	private Player theEnemy;
	private Player me;
	private String name;

	private Map map;

	public MyPlayer(String name, String host, int port) {
		this.name = name;

		// Creates the connection to the server
		this.client = new SkyportClient(host, port);
		this.client.connect();
		this.client.sendHandshake(this.name);

		// Requests the chosen weapons
		this.client.sendLoadout( new Droid(),new Mortar());
	}
	
	public void setNewTurn(){
		actionsLeft = 3;
		state = this.client.nextTurn(this.name);
		me = state.getPlayers().get(0);
		theEnemy = state.getPlayers().get(1);
		map = state.getMap();
	}
	
	public void mine(){
		if(actionsLeft <= 0)
			return;
		
		Tile data = map.getData(me.getPosition());
		System.out.println("CHECKING FOR MINE!");
		if(data.equals(Tile.EXPLOSIUM)){
			client.mine();
			explosium++;
			actionsLeft--;
			if(actionsLeft <= 0)
				return;
			client.mine();
			explosium++;
			actionsLeft--;
		}
		if(actionsLeft <= 0)
			return;
		
		if(data.equals(Tile.SCRAP)){
			client.mine();
			actionsLeft--;
			scrap++;
			if(actionsLeft <= 0)
				return;
			client.mine();
			actionsLeft--;
			scrap++;
		}
		if(data.equals(Tile.RUBIDIUM)){
			client.mine();
			actionsLeft--;
			rubidium++;
			if(actionsLeft <= 0)
				return;
			client.mine();
			actionsLeft--;
			rubidium++;
		}
	}
	
	public boolean onMineableTile(){
		Tile data = map.getData(me.getPosition());
		return data.equals(Tile.EXPLOSIUM) || data.equals(Tile.SCRAP);
	}
	
	public void upgrade(){
		if(actionsLeft <= 0)
			return;
		
		if(scrap >= 5 || (me.getSecondary().getLevel() < 2 && scrap >= 4)
		  && me.getSecondary().getLevel() < 3){
			actionsLeft--;
			client.upgrade(me.getSecondary());
			if(me.getSecondary().getLevel() < 2){
				scrap -= 4;
			} else {
				scrap -= 5;
			}
		}
		if(actionsLeft <= 0)
			return;
		if(explosium >= 5 || (me.getPrimary().getLevel() < 2 && explosium >= 4)
		  && me.getPrimary().getLevel() < 3){
			actionsLeft--;
			client.upgrade(me.getPrimary());
			if(me.getPrimary().getLevel() < 2){
				explosium -= 4;
			} else {
				explosium -= 5;
			}
		}
	}
	
	private void moveToMine() {
		if(actionsLeft <= 0)
			return;
		System.out.println("CONSIDERING MINING MOVES!");
		
		BFSIterator it = map.iterator(me.getPosition());
		
		while(it.hasNext()){
			Point curr = it.next();
			Tile data = map.getData(curr);
			if(data.equals(me.getPrimary().getResource())
			|| data.equals(me.getSecondary().getResource())){
				goTowards(it.getPath(curr));
				return;
			}
		}
	}
	
	public void shoot(Weapon wep){
		if(actionsLeft <= 0)
			return;
		System.out.println("CONSIDERING SHOOTING MOVES!");
		
		ShootActionIterator primaIt = wep.iterator(me.getPosition(), map);
		while(primaIt.hasNext()){
			Point shootAt = primaIt.next();
			if(shootAt.equals(theEnemy.getPosition())){
				System.out.println("SHOOTING AT THE FUCKERS!");
				primaIt.getCurrentAction().perform(client);
				actionsLeft = 0;
				return;
			}
		}
	}
	
	private void moveToEnemy() {
		if(actionsLeft <= 0)
			return;
		System.out.println("KNIFES, GUNS, FIGHT!!");
		Point to = theEnemy.getPosition();
		BFSIterator it = map.iterator(me.getPosition());
		while(it.hasNext()){
			Point curr = it.next();
			if(curr.equals(to)){
				goTowards(it.getPath(curr));
				return;
			}
		}
	}
	
	private void goTowards(List<Direction> path){
		for(int i=0; i < path.size() && actionsLeft-- > 0; i++){
			System.out.println("OMG IM ON THE MOVE: " + path.get(i));
			client.move(path.get(i));
		}
	}
	

	private void moveAnywhere() {
		System.out.println("OMG I DIDNT DO ANYTHING THIS ROUND!!! PANIC!!");
		if(actionsLeft <= 0)
			return;

		List<Point> mvTo = map.neighbours(me.getPosition());
		Random rnd = new Random();
		Point goTo = mvTo.get(rnd.nextInt(mvTo.size()));
		client.move(me.getPosition().direction(goTo));
		actionsLeft--;
	}

	public void run() {
		do {
			setNewTurn();
			
			mine();
			upgrade();
			shoot(me.getPrimary());
			shoot(me.getSecondary());
			
			if(!onMineableTile()){
				moveToMine();
				mine();
			}
			
			if(!onMineableTile()){
				moveToEnemy();
			}
			
			if(actionsLeft >= 3){
				moveAnywhere();
			}

		} while (state != null);
	}
}
