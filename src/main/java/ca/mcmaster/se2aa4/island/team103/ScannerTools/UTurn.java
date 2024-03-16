package ca.mcmaster.se2aa4.island.team103.ScannerTools;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.Drone;
import ca.mcmaster.se2aa4.island.team103.TurnDirection;

public class UTurn {

	public JSONObject performUTurn(Drone drone, TurnDirection turn){
		JSONObject decision = new JSONObject();

		if(turn.equals(TurnDirection.Left)){
			decision.put("response",drone.turnLeft());
			decision.put("done",true);
		}else{
			decision.put("response",drone.turnRight());
			decision.put("done",true);
		}
		
		return decision;
	}	
}
