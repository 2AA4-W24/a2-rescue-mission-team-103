package ca.mcmaster.se2aa4.island.team103;

import java.util.List;
import java.util.ArrayList;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;

public class IslandRecon {
	private final Logger logger = LogManager.getLogger(); 

	private enum HLPhase{
		Echo,
		Decision,
		Travel,
		TurnWait,
		Turn,
		SpecialTurn,
		SpecialTurn2
	}

	private enum TravelPhase{
		Scan,
		Move,
		Decision
	}

	private enum TurnWaitPhase{
		EchoSide,
		Move
	}

	private enum TurnStages{
		TurnStage1,
		TurnStage2,
		TurnStage3,
		TurnStage4,
		TurnStage5
	}

	private enum TurnStatus{
		Left,
		Right
	}

	// High-level, overview phase
	private HLPhase HLstatus = HLPhase.Echo;

	// Secondary phases to track stage within high-level phase
	private TravelPhase travelStatus = TravelPhase.Move;
	private TurnWaitPhase turn_wait = TurnWaitPhase.Move;
	private TurnStages turn_status = TurnStages.TurnStage1;

	// Tracking next turn direction
	private TurnStatus turn_direction;

	// Handling edge cases for end of map
	private TurnStatus special_turn_direction = TurnStatus.Left;

	
	// Extra trackers for handling edge case
	private int moves_since_last_special = 0;
	private boolean counter_activator = false;
	private int SCAN_NUM = 1;

	private boolean first_iter = true;

	public Optional<JSONObject> islandScan(Drone drone, ResponseHistory respHistory){

		// Return JSON Object
		JSONObject decision = new JSONObject();
		
		// Tracking logs
		logger.info("IN PHASE, {}", HLstatus);
		logger.info("SCAN_NUM, {}", SCAN_NUM);
		if(counter_activator){
			moves_since_last_special++;
		}

		if(first_iter){
			if(drone.getHeading().equals(Direction.EAST) || drone.getHeading().equals(Direction.SOUTH)){
				turn_direction = TurnStatus.Left;
			}else{
				turn_direction = TurnStatus.Right;
			}
			first_iter = false;
		}

		switch(HLstatus){
			case Echo:
				// Resetting turn status.
				turn_status = TurnStages.TurnStage1;
				decision = drone.scanForward();
				HLstatus = HLPhase.Decision;
				break;

			case Decision:
				if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
					if(SCAN_NUM == 1){
						if(turn_direction.equals(TurnStatus.Right)){
							decision = drone.turnLeft();
							special_turn_direction = TurnStatus.Left;
						}else{
							decision = drone.turnRight();
							special_turn_direction = TurnStatus.Right;
						}
						HLstatus = HLPhase.SpecialTurn;
						counter_activator = true;
						SCAN_NUM++;
					}else if(SCAN_NUM == 2 && moves_since_last_special < 20) {
						if(turn_direction.equals(TurnStatus.Right)){
							decision = drone.turnRight();
							special_turn_direction = TurnStatus.Right;
						}else{
							decision = drone.turnLeft();
							special_turn_direction = TurnStatus.Left;
						}
						HLstatus = HLPhase.SpecialTurn2;
					}else if(SCAN_NUM == 2){
						return Optional.empty();
					}
				}else{
					decision = drone.scan();
					HLstatus = HLPhase.Travel;
					travelStatus = TravelPhase.Move;
				}
				break;
			case Travel:
				switch(travelStatus){
					case Scan:
						decision = drone.scan();
						travelStatus = TravelPhase.Move;
						break;
					case Move:
						JSONArray biomesObj = respHistory.getLast().getJSONObject("extras").getJSONArray("biomes");
						
						if(biomesObj.getString(0).equals("OCEAN") && biomesObj.length() == 1){
							decision = drone.scanForward();
							travelStatus = TravelPhase.Decision;
							break;
						}
						
						if(travelStatus != TravelPhase.Decision){
							decision = drone.flyForwards();
							travelStatus = TravelPhase.Scan;
						}
						break;
					case Decision:
						if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
							if(turn_direction.equals(TurnStatus.Right)){
								decision = drone.scanRight();
							}else{
								decision = drone.scanLeft();
							}
							HLstatus = HLPhase.TurnWait;
						}else{
							decision = drone.flyForwards();
							travelStatus = TravelPhase.Scan;
						}
						break;
				}
				break;
			case TurnWait:
				travelStatus = TravelPhase.Move;
				switch(turn_wait){

					case EchoSide:
						if(turn_direction.equals(TurnStatus.Right)){
							decision = drone.scanRight();
						}else{
							decision = drone.scanLeft();
						}
						turn_wait = TurnWaitPhase.Move;
						break;

					case Move:
						if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject("extras").getString("found").equals("GROUND") && respHistory.getLast().getJSONObject("extras").getInt("range") > 2)){
							if(turn_direction.equals(TurnStatus.Right)){
								decision = drone.turnRight();
							}else{
								decision = drone.turnLeft();
							}
							HLstatus = HLPhase.Turn;
						}else{
							decision = drone.flyForwards();
							turn_wait = TurnWaitPhase.EchoSide;
						}
						break;
				}
				break;
			case Turn:
				if(turn_direction.equals(TurnStatus.Left)){
					decision = drone.turnLeft();
					turn_direction = TurnStatus.Right;
				}else{
					decision = drone.turnRight();
					turn_direction = TurnStatus.Left;
				}
				HLstatus = HLPhase.Echo;
				break;
			case SpecialTurn:
				turn_wait = TurnWaitPhase.Move;
				switch(turn_status){
					case TurnStage1:
						decision = drone.flyForwards();
						turn_status = TurnStages.TurnStage2;
						break;
					case TurnStage2:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						turn_status = TurnStages.TurnStage3;
						break;
					case TurnStage3:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						turn_status = TurnStages.TurnStage4;
						break;
					case TurnStage4:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						if(turn_direction.equals(TurnStatus.Left)){
							turn_direction = TurnStatus.Right;
						}else{
							turn_direction = TurnStatus.Left;
						}
						turn_status = TurnStages.TurnStage1;
						HLstatus = HLPhase.Echo;
						break;
					case TurnStage5:
						break;
				}
				break;
			case SpecialTurn2:
				turn_wait = TurnWaitPhase.Move;
				switch(turn_status){
					case TurnStage1:
						decision = drone.flyForwards();
						turn_status = TurnStages.TurnStage2;
						break;
					case TurnStage2:
						decision = drone.flyForwards();
						turn_status = TurnStages.TurnStage3;
						break;
					case TurnStage3:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();
						}else{
							decision = drone.turnRight();	
						}
						turn_status = TurnStages.TurnStage4;
						break;
					case TurnStage4:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						turn_status = TurnStages.TurnStage5;
						break;
					case TurnStage5:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						turn_status = TurnStages.TurnStage1;
						HLstatus = HLPhase.Echo;
						break;
				}
				break;
			
		}
		return Optional.of(decision);
	}	
	
	
}

