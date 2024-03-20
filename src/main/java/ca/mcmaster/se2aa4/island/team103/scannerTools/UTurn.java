package ca.mcmaster.se2aa4.island.team103.scannerTools;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.Drone;
import ca.mcmaster.se2aa4.island.team103.TurnDirection;

public class UTurn {

	private enum UTurnStatus{
		UTURN1,
		UTURN2
	}

	private static final String RESPONSE = "response";
	private static final String DONE = "done";

	UTurnStatus UTurnstatus = UTurnStatus.UTURN1;

	public JSONObject performUTurn(Drone drone, TurnDirection turn){
		JSONObject decision = new JSONObject();
		if(UTurnstatus.equals(UTurnStatus.UTURN1)){
			if(turn.equals(TurnDirection.LEFT)){
				decision.put(RESPONSE,drone.turnLeft());
				decision.put(DONE,false);
			}else{
				decision.put(RESPONSE,drone.turnRight());
				decision.put(DONE,false);
			}
			UTurnstatus = UTurnStatus.UTURN2;
		}else{
			if(turn.equals(TurnDirection.LEFT)){
				decision.put(RESPONSE,drone.turnLeft());
				decision.put(DONE,true);
			}else{
				decision.put(RESPONSE,drone.turnRight());
				decision.put(DONE,true);
			}
			UTurnstatus = UTurnStatus.UTURN1;
		}
		
		return decision;
	}	
}
