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
		SpecialTurn
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
		TurnStage2
	}

	private enum TurnStatus{
		Left,
		Right
	}

	private HLPhase HLstatus = HLPhase.Echo;
	private TravelPhase travelStatus = TravelPhase.Move;
	private TurnWaitPhase turn_wait = TurnWaitPhase.Move;
	private TurnStages turn_status = TurnStages.TurnStage1;
	private TurnStatus turn_direction = TurnStatus.Left;
	private TurnStatus special_turn_direction = TurnStatus.Left;

	private int SCAN_NUM = 1;
	private int special_turn_counter = 0;

	public Optional<JSONObject> islandScan(Drone drone, ResponseHistory respHistory){
		JSONObject decision = new JSONObject();

		logger.info("IN PHASE, {}", HLstatus);
		logger.info("SCAN_NUM, {}", SCAN_NUM);
		logger.info("SPECIAL: {}",special_turn_counter);

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
						SCAN_NUM++;
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
						for(int i=0; i<biomesObj.length(); i++){
							if(biomesObj.getString(i).equals("OCEAN")){
								decision = drone.scanForward();
								travelStatus = TravelPhase.Decision;
								break;
							}
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
						if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE") || (respHistory.getLast().getJSONObject("extras").getString("found").equals("GROUND") && respHistory.getLast().getJSONObject("extras").getInt("range")!=0)){
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
				special_turn_counter++;
				turn_wait = TurnWaitPhase.Move;
				switch(turn_status){
					case TurnStage1:
						decision = drone.flyForwards();
						turn_status = TurnStages.TurnStage2;
					break;
					case TurnStage2:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnRight();

						}else{
							decision = drone.turnLeft();
							
						}
						turn_status = TurnStages.TurnStage1;
						HLstatus = HLPhase.Echo;
						if(turn_direction.equals(TurnStatus.Left)){
							turn_direction = TurnStatus.Right;
						}else{
							turn_direction = TurnStatus.Left;
						}
						break;
				}
			
		}
		return Optional.of(decision);
	}	
	
	
}

