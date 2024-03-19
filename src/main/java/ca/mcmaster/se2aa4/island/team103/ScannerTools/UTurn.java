package ca.mcmaster.se2aa4.island.team103.ScannerTools;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.Drone;
import ca.mcmaster.se2aa4.island.team103.TurnDirection;

public class UTurn {

	private enum UTurnStatus{
		UTurn1,
		UTurn2
	}

	UTurnStatus UTurnstatus = UTurnStatus.UTurn1;

	public JSONObject performUTurn(Drone drone, TurnDirection turn){
		JSONObject decision = new JSONObject();
		if(UTurnstatus.equals(UTurnStatus.UTurn1)){
			if(turn.equals(TurnDirection.Left)){
				decision.put("response",drone.turnLeft());
				decision.put("done",false);
			}else{
				decision.put("response",drone.turnRight());
				decision.put("done",false);
			}
			UTurnstatus = UTurnStatus.UTurn2;
		}else{
			if(turn.equals(TurnDirection.Left)){
				decision.put("response",drone.turnLeft());
				decision.put("done",true);
			}else{
				decision.put("response",drone.turnRight());
				decision.put("done",true);
			}
			UTurnstatus = UTurnStatus.UTurn1;
		}
		
		return decision;
	}	
}
