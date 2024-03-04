package ca.mcmaster.se2aa4.island.team103;

import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IslandRecon {
	/* Intended to perform a full scan of the island. Goes lengthwise until the end, then performs three turns of one direction, then one of the opposite,
	 * such that it ends up right next to where it turned around and does not miss any potential scan area.
	*/
	private final Logger logger = LogManager.getLogger(); 
	private List<JSONObject> scanResults = new ArrayList<JSONObject>();
	private enum Status {
		Echo,
		EchoDir,
		Move,
		Scan,
		TurnStage1,
		TurnStage2,
		TurnStage3,
		TurnStage4,
		TurnStage5
	}
	private Status status = Status.Scan;
	private String turn_status = "left";
	private String scan_status = "left";
	private List<Boolean> scanning_over = new ArrayList<Boolean>();
	private boolean stop_scan = true;

	public JSONObject islandScan(Drone drone, ResponseHistory respHistory){
		
		stop_scan = true;
		JSONObject decision = new JSONObject();
		switch(status){
			case Scan:
				decision.put("response",drone.scan());
				status = Status.EchoDir;
				break;
			case EchoDir:
				if(scan_status.equals("left")){
					decision.put("response",drone.scanLeft());
				}else{
					decision.put("response",drone.scanRight());
				}
				status = Status.Echo;
				break;
			case Echo:
				decision.put("response",drone.scanForward());
				status = Status.Move;
				break;
			case Move:
				// If the land is out of range we have reached the end of the island and can turn around. Also appending second last (scan left/right) item to scan results.
				// This will be used to see if the scanning can be completed or not.
				scanResults.add(respHistory.getItems(-2).get(0));
				if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
					decision.put("response",drone.flyForwards());
					status = Status.TurnStage1;
				}else{
					decision.put("response",drone.flyForwards());
					status = Status.Scan;
				}
				break;
			case TurnStage1:
				for(int i=0; i<scanResults.size(); i++){
					scanning_over.add(scanResults.get(i).getJSONObject("extras").getString("found").equals("OUT_OF_RANGE"));
				}
				for(int i=0; i<scanning_over.size(); i++){
					if(!scanning_over.get(i)){
						stop_scan = false;
					}
				}
				
				if(turn_status.equals("left")){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());
				}
				status = Status.TurnStage3;
				break;
			case TurnStage2:
				decision.put("response",drone.flyForwards());
				status = Status.TurnStage3;
				break;
			case TurnStage3:
				scanning_over = new ArrayList<Boolean>();
				scanResults = new ArrayList<JSONObject>();
				if(turn_status.equals("left")){
					decision.put("response",drone.turnLeft());
					turn_status = "right";
					scan_status = "right";
				}else{
					decision.put("response",drone.turnRight());
					turn_status = "left";
					scan_status = "left";
					
				}
				status = Status.Scan;
				break;
			case TurnStage4:
				if(turn_status.equals("left")){
					decision.put("response",drone.turnLeft());
				}else{
					decision.put("response",drone.turnRight());
				}
				status = Status.TurnStage5;
				break;
			case TurnStage5:
				// Changing the direction of the turn so that it does not travel in a loop.
				if(turn_status.equals("left")){
					decision.put("response",drone.turnRight());
					turn_status = "right";
					
				}else{
					decision.put("response",drone.turnLeft());
					turn_status = "left";
					
				}
				status = Status.Scan;
				break;
			
		}
		if(stop_scan && status.equals(Status.TurnStage3)){
			decision.put("over","true");
		}
		return decision;
		
	}
}
