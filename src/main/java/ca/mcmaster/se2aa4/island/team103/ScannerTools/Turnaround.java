package ca.mcmaster.se2aa4.island.team103.ScannerTools;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.*;

public class Turnaround {
	private enum TurnStatus{
		TURNSTAGE1,
		TURNSTAGE2,
		TURNSTAGE3,
		TURNSTAGE4,
		TURNSTAGE5,
		TURNSTAGE6,
		TURNSTAGE7
	}

	private enum TurnWait {
		Echo,
		Move
	}

	private final String RESPONSE = "response";
	private final String EXTRAS = "extras";
	private final String FOUND = "found";

	TurnStatus turn_status = TurnStatus.TURNSTAGE1;
	TurnWait special_turnwait = TurnWait.Echo;
	

	public JSONObject specialTurn(Drone drone, History<JSONObject> respHistory, TurnDirection special_turn_direction){
		JSONObject decision = new JSONObject();
		switch(turn_status){
			case TURNSTAGE1:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				turn_status = TurnStatus.TURNSTAGE2;
				break;
			case TURNSTAGE2:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				turn_status = TurnStatus.TURNSTAGE3;
				break;
			case TURNSTAGE3:
				switch(special_turnwait){
					case Echo:
						if(special_turn_direction.equals(TurnDirection.LEFT)){
							decision.put(RESPONSE,drone.echoLeft());
						}else{
							decision.put(RESPONSE,drone.echoRight());
						}
						special_turnwait = TurnWait.Move;
						break;
					case Move:
						if(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("GROUND") && respHistory.getLast().getJSONObject(EXTRAS).getInt("range") > 2)){
							if(special_turn_direction.equals(TurnDirection.RIGHT)){
								decision.put(RESPONSE,drone.turnRight());
							}else{
								decision.put(RESPONSE,drone.turnLeft());
							}
							turn_status = TurnStatus.TURNSTAGE4;
						}else{
							decision.put(RESPONSE,drone.flyForwards());
						}
						special_turnwait = TurnWait.Echo;
						break;
				}
				break;
			case TURNSTAGE4:
				decision.put(RESPONSE,drone.flyForwards());
				turn_status = TurnStatus.TURNSTAGE5;
				break;
			case TURNSTAGE5:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				turn_status = TurnStatus.TURNSTAGE6;
				break;
			case TURNSTAGE6:
				decision.put("done",true);
				turn_status = TurnStatus.TURNSTAGE1;
				break;
			case TURNSTAGE7:
				break;
		}
		return decision;
	}

	public JSONObject specialTurn2(Drone drone, History<JSONObject> respHistory, TurnDirection special_turn_direction){
		JSONObject decision = new JSONObject();
		switch(turn_status){
			case TURNSTAGE1:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				turn_status = TurnStatus.TURNSTAGE2;
				break;
			case TURNSTAGE2:
				decision.put(RESPONSE,drone.flyForwards());
				turn_status = TurnStatus.TURNSTAGE3;
				break;
			case TURNSTAGE3:
				decision.put(RESPONSE,drone.flyForwards());
				turn_status = TurnStatus.TURNSTAGE4;
				break;
			case TURNSTAGE4:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				turn_status = TurnStatus.TURNSTAGE5;
				break;
			case TURNSTAGE5:
				switch(special_turnwait){
					case Echo:
						if(special_turn_direction.equals(TurnDirection.LEFT)){
							decision.put(RESPONSE,drone.echoLeft());
						}else{
							decision.put(RESPONSE,drone.echoRight());
						}
						special_turnwait = TurnWait.Move;
						break;
					case Move:
						if(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("GROUND") && respHistory.getLast().getJSONObject(EXTRAS).getInt("range") > 2)){
							if(special_turn_direction.equals(TurnDirection.RIGHT)){
								decision.put(RESPONSE,drone.turnRight());
							}else{
								decision.put(RESPONSE,drone.turnLeft());
							}
							turn_status = TurnStatus.TURNSTAGE6;
						}else{
							decision.put(RESPONSE,drone.flyForwards());
						}
						special_turnwait = TurnWait.Echo;
						break;
				}
				break;
			case TURNSTAGE6:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());
				}
				turn_status = TurnStatus.TURNSTAGE7;
				break;
			case TURNSTAGE7:
				decision.put("done",true);
				turn_status = TurnStatus.TURNSTAGE1;
				break;
		}
		return decision;
	}
}
