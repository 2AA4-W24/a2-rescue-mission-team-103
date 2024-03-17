package ca.mcmaster.se2aa4.island.team103.ScannerTools;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.*;

public class Turnaround {
	private enum TurnStatus{
		TurnStage1,
		TurnStage2,
		TurnStage3,
		TurnStage4,
		TurnStage5,
		TurnStage6,
		TurnStage7
	}

	private enum TurnWait {
		Echo,
		Move
	}

	TurnStatus turn_status = TurnStatus.TurnStage1;
	TurnWait special_turnwait = TurnWait.Echo;
	

	public JSONObject specialTurn(Drone drone, History<JSONObject> respHistory, TurnDirection special_turn_direction){
		JSONObject decision = new JSONObject();
		switch(turn_status){
			case TurnStage1:
				if(special_turn_direction.equals(TurnDirection.Left)){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());	
				}
				turn_status = TurnStatus.TurnStage2;
				break;
			case TurnStage2:
				if(special_turn_direction.equals(TurnDirection.Left)){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());	
				}
				turn_status = TurnStatus.TurnStage3;
				break;
			case TurnStage3:
				switch(special_turnwait){
					case Echo:
						if(special_turn_direction.equals(TurnDirection.Left)){
							decision.put("response",drone.echoLeft());
						}else{
							decision.put("response",drone.echoRight());
						}
						special_turnwait = TurnWait.Move;
						break;
					case Move:
						if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject("extras").getString("found").equals("GROUND") && respHistory.getLast().getJSONObject("extras").getInt("range") > 2)){
							if(special_turn_direction.equals(TurnDirection.Right)){
								decision.put("response",drone.turnRight());
							}else{
								decision.put("response",drone.turnLeft());
							}
							turn_status = TurnStatus.TurnStage4;
						}else{
							decision.put("response",drone.flyForwards());
						}
						special_turnwait = TurnWait.Echo;
						break;
				}
				break;
			case TurnStage4:
				decision.put("response",drone.flyForwards());
				turn_status = TurnStatus.TurnStage5;
				break;
			case TurnStage5:
				if(special_turn_direction.equals(TurnDirection.Left)){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());	
				}
				turn_status = TurnStatus.TurnStage6;
				break;
			case TurnStage6:
				decision.put("done",true);
				turn_status = TurnStatus.TurnStage1;
				break;
			case TurnStage7:
				break;
		}
		return decision;
	}

	public JSONObject specialTurn2(Drone drone, History<JSONObject> respHistory, TurnDirection special_turn_direction){
		JSONObject decision = new JSONObject();
		switch(turn_status){
			case TurnStage1:
				if(special_turn_direction.equals(TurnDirection.Left)){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());	
				}
				turn_status = TurnStatus.TurnStage2;
				break;
			case TurnStage2:
				decision.put("response",drone.flyForwards());
				turn_status = TurnStatus.TurnStage3;
				break;
			case TurnStage3:
				decision.put("response",drone.flyForwards());
				turn_status = TurnStatus.TurnStage4;
				break;
			case TurnStage4:
				if(special_turn_direction.equals(TurnDirection.Left)){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());	
				}
				turn_status = TurnStatus.TurnStage5;
				break;
			case TurnStage5:
				switch(special_turnwait){
					case Echo:
						if(special_turn_direction.equals(TurnDirection.Left)){
							decision.put("response",drone.echoLeft());
						}else{
							decision.put("response",drone.echoRight());
						}
						special_turnwait = TurnWait.Move;
						break;
					case Move:
						if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject("extras").getString("found").equals("GROUND") && respHistory.getLast().getJSONObject("extras").getInt("range") > 2)){
							if(special_turn_direction.equals(TurnDirection.Right)){
								decision.put("response",drone.turnRight());
							}else{
								decision.put("response",drone.turnLeft());
							}
							turn_status = TurnStatus.TurnStage6;
						}else{
							decision.put("response",drone.flyForwards());
						}
						special_turnwait = TurnWait.Echo;
						break;
				}
				break;
			case TurnStage6:
				if(special_turn_direction.equals(TurnDirection.Left)){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());
				}
				turn_status = TurnStatus.TurnStage7;
				break;
			case TurnStage7:
				decision.put("done",true);
				turn_status = TurnStatus.TurnStage1;
				break;
		}
		return decision;
	}
}
