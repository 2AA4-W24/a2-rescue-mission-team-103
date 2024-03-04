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
	}

	private enum Special2TS{
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

	private HLPhase HLstatus = HLPhase.Echo;
	private TravelPhase travelStatus = TravelPhase.Move;
	private TurnWaitPhase turn_wait = TurnWaitPhase.Move;
	private TurnStages turn_status = TurnStages.TurnStage1;
	private TurnStatus turn_direction = TurnStatus.Left;
	private TurnStatus special_turn_direction = TurnStatus.Left;
	private Special2TS special2_td = Special2TS.TurnStage1;

	private int SCAN_NUM = 1;
	private int special_turn_counter = 0;
	private int edge_case = 0;
	private boolean counter_activator = false;
	private int counter = 0;

	public Optional<JSONObject> islandScan(Drone drone, ResponseHistory respHistory){
		JSONObject decision = new JSONObject();
		if(counter_activator){
			counter++;
		}
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
						counter_activator = true;
						SCAN_NUM++;
					}else if(SCAN_NUM == 2) {
						if(edge_case == 0 && counter < 20){
							if(turn_direction.equals(TurnStatus.Right)){
								decision = drone.turnLeft();
								special_turn_direction = TurnStatus.Left;
							}else{
								decision = drone.turnRight();
								special_turn_direction = TurnStatus.Right;
							}
							HLstatus = HLPhase.SpecialTurn2;
							edge_case++;
						}else{
							return Optional.empty();
						}
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
				special_turn_counter++;
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
				}
				break;
			case SpecialTurn2:
				special_turn_counter++;
				turn_wait = TurnWaitPhase.Move;
				switch(special2_td){
					case TurnStage1:
						decision = drone.flyForwards();
						special2_td = Special2TS.TurnStage2;
						break;
					case TurnStage2:
						decision = drone.flyForwards();
						special2_td = Special2TS.TurnStage3;
						break;
					case TurnStage3:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						special2_td = Special2TS.TurnStage4;
						break;
					case TurnStage4:
						if(special_turn_direction.equals(TurnStatus.Left)){
							decision = drone.turnLeft();

						}else{
							decision = drone.turnRight();	
						}
						special2_td = Special2TS.TurnStage5;
						break;
					case TurnStage5:
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
						special2_td = Special2TS.TurnStage1;
						HLstatus = HLPhase.Echo;
						break;
				}
				break;
			
		}
		return Optional.of(decision);
	}	
	
	
}

