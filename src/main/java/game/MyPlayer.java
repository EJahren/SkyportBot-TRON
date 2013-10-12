package game;

import java.util.ArrayList;
import java.util.List;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import skyport.api.SkyportClient;
import skyport.api.game.Direction;
import skyport.api.game.GameState;
import skyport.api.game.Map;
import skyport.api.game.Player;
import skyport.api.game.Point;
import skyport.api.game.Weapon;
import skyport.api.game.WeaponType;
import skyport.api.game.Tile;

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
			int actionsLeft = 3;
			state = this.client.nextTurn(this.name);
			me = state.getPlayers().get(0);
			Player theEnemy = state.getPlayers().get(1);
			map = state.getMap();
			Tile data = map.getData(me.getPosition());
			boolean onMinableTile = false;
			
			System.out.println("CHECKING FOR MINE!");
			if(data.equals(Tile.EXPLOSIUM)){
				client.mine();
				actionsLeft--;
				explosium++;
				onMinableTile = true;
			}
			
			if(data.equals(Tile.RUBIDIUM)){
				client.mine();
				actionsLeft--;
				rubidium++;
				onMinableTile = true;
			}
			
			if(rubidium >= 5 || (me.getSecondary().getLevel() < 2 && rubidium >= 3)){
				actionsLeft--;
				client.upgrade("laser");
			}
			
			if(explosium >= 3 || (me.getPrimary().getLevel() < 2 && explosium >= 3)){
				actionsLeft--;
				client.upgrade("mortar");
			}
			
			Player me = state.getPlayers().get(0);
			Map map = state.getMap();
			
			List<Action> hitActions = new ArrayList<Action>();
			
			System.out.println("GETTING HITTABLE ENEMIES!");
			List<Player> primaryHittable = getHittable(state,me.getPosition(),me.getPrimary(),map);
			List<Player> secondaryHittable = getHittable(state,me.getPosition(),me.getSecondary(),map);
			
			if(primaryHittable.size() != 0 || secondaryHittable.size() != 0){
				
				System.out.println("GUNS, KINFES, FIGHT!!!!");
			}
			
			System.out.println("GETTING HIT ACTION!");
			for(Player p : primaryHittable){
				Action act = Utilities.getHitAction(me.getPosition(),me.getPrimary(),p.getPosition());
				if(act != null)
					hitActions.add(act);
			}
			
			for(Player p : secondaryHittable){
				Action act = Utilities.getHitAction(me.getPosition(),me.getSecondary(),p.getPosition());
				if(act != null)
					hitActions.add(act);
			}
			
			for(Action a: hitActions){
				if(actionsLeft <= 0){
					System.out.println("NO MOVES LEFT FOR HIT!");
					break;
				}
				a.perform(client);
				actionsLeft=0;
			}
			
			if (!onMinableTile) {
				System.out.println("GETTING MINE POINTS!");
				System.out.println("LENGTHSBITCHES: " + map.getjLength() + ":"
						+ map.getkLength());
				List<Point> minePoints = new ArrayList<Point>();
				for (int j = 0; j < map.getjLength(); j++) {
					for (int k = 0; k < map.getkLength(); k++) {
						System.out.println(j + ":" + k);
						Tile t = map.getData(new Point(j, k));
						if (t.equals(Tile.EXPLOSIUM) || t.equals(Tile.RUBIDIUM)) {
							minePoints.add(new Point(j, k));
						}
					}
				}

				System.out.println("MOVING TO MINE POINTS!");
				System.out.println("NUMBER OF MINEPOINTS: " + minePoints.size());
				
				if(minePoints.size() != 0){
					int min = me.getPosition().distance(minePoints.get(0));
					Point p = minePoints.get(0);
					for(int i = 0;i < minePoints.size();i++){
						int dist = me.getPosition().distance(minePoints.get(i));
						if(dist < min){		
							min = dist;
							p = minePoints.get(i);
						}
					}

					List<Direction> mvs = Utilities.bfs(map, me.getPosition(),
							p);

					if (mvs == null) {
						System.out.println("CANT MOVE TO MINEPOINT, WAT?");
						continue;
					}

					for (Direction dir : mvs) {
						if (actionsLeft <= 0) {
							System.out.println("NO MOVES LEFT FOR MINE!");
							break;
						}
						client.move(dir);
					}

				} else {
					List<Direction> mvs = Utilities.bfs(map, me.getPosition(),
							state.getPlayers().get(1).getPosition());

					if (mvs == null) {
						System.out.println("CANT MOVE TOWARDS ENEMY, WAT?");
						continue;
					}

					for (Direction dir : mvs) {
						if (actionsLeft <= 0) {
							System.out.println("NO MOVES LEFT FOR ENEMY!");
							break;
						}
						client.move(dir);
					}
					
				}
			}
			

		} while (state != null);
	}
	
	private static List<Player> getHittable(GameState state,Point p, Weapon w, Map m){
		List<Point> shootable = w.inRange(p, m);
		
		for(Point prt : shootable){
			System.out.println(prt.string());
		}
		
		List<Player> hittable = new ArrayList<Player>();
		
		for(int i = 1; i < state.getPlayers().size();i++){
			Player pl = state.getPlayers().get(i);
			if(shootable.contains(pl.getPosition())){
				hittable.add(pl);
			}

		}
		return hittable;
	}
}
