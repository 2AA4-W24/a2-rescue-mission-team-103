package ca.mcmaster.se2aa4.island.team103.islandScanning;
import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.history.History;

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
		ECHO,
		MOVE
	}

	private static final String RESPONSE = "response";
	private static final String EXTRAS = "extras";
	private static final String FOUND = "found";

	TurnStatus TURNSTATUS = TurnStatus.TURNSTAGE1;
	TurnWait SPECIALTURNWAIT = TurnWait.ECHO;
	

	// Special UTurn that does not miss a strip of land to be done at the end of the first scan pass. Uses a Turn-Forward-Turn-Turn-Turn structure, along
	// with a special TurnWait section such that the turn does not cause the drone to miss land.
	public JSONObject specialTurn(Drone drone, History<JSONObject> respHistory, TurnDirection special_turn_direction){
		JSONObject decision = new JSONObject();
		switch(TURNSTATUS){

			case TURNSTAGE1:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				TURNSTATUS = TurnStatus.TURNSTAGE2;
				break;

			case TURNSTAGE2:
				decision.put(RESPONSE,drone.flyForwards());
				TURNSTATUS = TurnStatus.TURNSTAGE3;
				break;

			case TURNSTAGE3:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				TURNSTATUS = TurnStatus.TURNSTAGE4;
				break;

			case TURNSTAGE4:

				switch(SPECIALTURNWAIT){

					case ECHO:
						if(special_turn_direction.equals(TurnDirection.LEFT)){
							decision.put(RESPONSE,drone.echoLeft());
						}else{
							decision.put(RESPONSE,drone.echoRight());
						}
						SPECIALTURNWAIT = TurnWait.MOVE;
						break;

					case MOVE:
						if(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("GROUND") && respHistory.getLast().getJSONObject(EXTRAS).getInt("range") > 2)){
							if(special_turn_direction.equals(TurnDirection.RIGHT)){
								decision.put(RESPONSE,drone.turnRight());
							}else{
								decision.put(RESPONSE,drone.turnLeft());
							}
							TURNSTATUS = TurnStatus.TURNSTAGE5;
						}else{
							decision.put(RESPONSE,drone.flyForwards());
						}
						SPECIALTURNWAIT = TurnWait.ECHO;
						break;
				}
				break;

			case TURNSTAGE5:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				TURNSTATUS = TurnStatus.TURNSTAGE6;
				break;

			case TURNSTAGE6:
				decision.put("done",true);
				TURNSTATUS = TurnStatus.TURNSTAGE1;
				break;

			case TURNSTAGE7:
				break;
		}
		return decision;
	}

	// Boundary case where if the first scan pass goes until the last column, a second special turn will be required. Requires an extra forward move.
	public JSONObject specialTurn2(Drone drone, History<JSONObject> respHistory, TurnDirection special_turn_direction){
		JSONObject decision = new JSONObject();
		switch(TURNSTATUS){

			case TURNSTAGE1:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				TURNSTATUS = TurnStatus.TURNSTAGE2;
				break;

			case TURNSTAGE2:
				decision.put(RESPONSE,drone.flyForwards());
				TURNSTATUS = TurnStatus.TURNSTAGE3;
				break;

			case TURNSTAGE3:
				decision.put(RESPONSE,drone.flyForwards());
				TURNSTATUS = TurnStatus.TURNSTAGE4;
				break;

			case TURNSTAGE4:
				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());	
				}
				TURNSTATUS = TurnStatus.TURNSTAGE5;
				break;

			case TURNSTAGE5:
				switch(SPECIALTURNWAIT){

					case ECHO:
						if(special_turn_direction.equals(TurnDirection.LEFT)){
							decision.put(RESPONSE,drone.echoLeft());
						}else{
							decision.put(RESPONSE,drone.echoRight());
						}
						SPECIALTURNWAIT = TurnWait.MOVE;
						break;

					case MOVE:
						if(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("GROUND") && respHistory.getLast().getJSONObject(EXTRAS).getInt("range") > 2)){
							if(special_turn_direction.equals(TurnDirection.RIGHT)){
								decision.put(RESPONSE,drone.turnRight());
							}else{
								decision.put(RESPONSE,drone.turnLeft());
							}
							TURNSTATUS = TurnStatus.TURNSTAGE6;
						}else{
							decision.put(RESPONSE,drone.flyForwards());
						}
						SPECIALTURNWAIT = TurnWait.ECHO;
						break;
				}
				break;

			case TURNSTAGE6:

				if(special_turn_direction.equals(TurnDirection.LEFT)){
					decision.put(RESPONSE,drone.turnLeft());
				}else{
					decision.put(RESPONSE,drone.turnRight());
				}
				TURNSTATUS = TurnStatus.TURNSTAGE7;
				break;
				
			case TURNSTAGE7:
				decision.put("done",true);
				TURNSTATUS = TurnStatus.TURNSTAGE1;
				break;
		}
		return decision;
	}
}
