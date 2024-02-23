package ca.mcmaster.se2aa4.island.team103;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IslandRecon {
	/* Intended to perform a full scan of the island. Goes lengthwise until the end, then performs three turns of one direction, then one of the opposite,
	 * such that it ends up right next to where it turned around and does not miss any potential scan area.
	*/
	private final Logger logger = LogManager.getLogger(); 
	private enum Status {
		Unknown,
		Echo,
		Move,
		Scan,
		TurnStage1,
		TurnStage2,
		TurnStage3,
		TurnStage4,
		TurnStage5
	}
	private Status status = Status.Unknown;
	private String turn_status = "left";

	public JSONObject islandScan(Drone drone, ResponseHistory respHistory){

		JSONObject decision = new JSONObject();
		
		switch(status){
			case Unknown:
				decision = drone.turnLeft();
				status = Status.Scan;
				break;
			case Scan:
				decision = drone.scan();
				status = Status.Echo;
				break;
			case Echo:
				decision = drone.scanForward();
				status = Status.Move;
				break;
			case Move:
				// If the land is out of range we have reached the end of the island and can turn around.
				if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
					decision = drone.flyForwards();
					status = Status.TurnStage1;
				}else{
					decision = drone.flyForwards();
					status = Status.Scan;
				}
				break;
			case TurnStage1:
				if(turn_status.equals("left")){
					decision = drone.turnLeft();
				}else{
					decision = drone.turnRight();
				}
				status = Status.TurnStage2;
				break;
			case TurnStage2:
				decision = drone.flyForwards();
				status = Status.TurnStage3;
				break;
			case TurnStage3:
				if(turn_status.equals("left")){
					decision = drone.turnLeft();
					
				}else{
					decision = drone.turnRight();
					
				}
				status = Status.TurnStage4;
				break;
			case TurnStage4:
				if(turn_status.equals("left")){
					decision = drone.turnLeft();
				}else{
					decision = drone.turnRight();
				}
				status = Status.TurnStage5;
				break;
			case TurnStage5:
				// Changing the direction of the turn so that it does not travel in a loop.
				if(turn_status.equals("left")){
					decision = drone.turnRight();
					turn_status = "right";
				}else{
					decision = drone.turnLeft();
					turn_status = "left";
				}
				status = Status.Scan;
				break;
		}

		return decision;
		
	}
}
