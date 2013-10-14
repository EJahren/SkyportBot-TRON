package game;

import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.omg.CORBA.Current;

import com.google.common.collect.Lists;

import skyport.api.game.Direction;
import skyport.api.game.Map;
import skyport.api.game.Point;
import skyport.api.game.Weapon;

public class Utilities {
	public static Action getHitAction(Point from, Weapon primary, Point to) {
		switch(primary.getName()){
		case MORTAR:
			if(from.distance(to) <= primary.distance()){
				return new ShootMortarz(new Point(to.getJ()-from.getJ(),to.getK()-from.getK()));
			}
			break;
		case LASER:
			return new ShootLazerz(from.direction(to));
		}
		return null;

	}
	
	public static List<Direction> bfs(Map m, Point from,Point to){
		TreeMap<Point,Point> cameFrom = new TreeMap<Point,Point>();
		Queue<Point> q = new LinkedList<Point>();
		q.add(from);
		while(!q.isEmpty()){
			Point current = q.poll();
			if(current.equals(to)){
				return reconstructPath(from,cameFrom,to);
			}
			for(Point p : m.neighbors(current)){
				if(!cameFrom.containsKey(p) && !p.equals(current)){
					cameFrom.put(p,current);
					q.add(p);
				}
			}
		}
		return null;
	}

	private static List<Direction> reconstructPath( Point from,
			TreeMap<Point, Point> cameFrom, Point to) {
		
		List<Direction> path = new ArrayList<Direction>();
		while(!to.equals(from)){
			Point newTo = cameFrom.get(to);
			Direction dir = newTo.direction(to);
			path.add(dir);
			to = newTo;
		}
		return Lists.reverse(path);
	}

	public static Direction directionFromPoint(Point from, Point to) {
		if(from.equals(to)){
			return null;
		}
		if(from.getJ() > to.getJ()
		&& from.getK() > to.getK()){
			return Direction.up;
		} else if(from.getJ() > to.getJ()
		&& from.getK() == to.getK()){
			return Direction.leftUp;
		} else if(from.getJ() <= to.getJ()
		&& from.getK() > to.getK()){
			return Direction.rightUp;
		} else if(from.getJ() > to.getJ()
		&& from.getK() <= to.getK()){
			return Direction.leftDown;
		} else if(from.getJ() <= to.getJ()
		&& from.getK() < to.getK()){
			return Direction.rightDown;
		} if(from.getJ() < to.getJ()
		&& from.getK() < to.getK()){
			return Direction.down;
		}
		return null;
	}
}
