package game;

import java.util.List;

public class Decision {
	
	public Decision(double confidence, Action actions) {
		super();
		this.confidence = confidence;
		this.actions = actions;
	}
	
	double confidence;
	Action actions;
	
}
