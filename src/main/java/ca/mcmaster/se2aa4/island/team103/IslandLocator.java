package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IslandLocator {

	private String instruction = "scan";
	private final Logger logger = LogManager.getLogger();

	public JSONObject locate(Drone drone, ResponseHistory history) {
		JSONObject decision = new JSONObject();
		if(instruction.equals("scan")){
			logger.info("SCAN");
			decision.put("action","scan");
			instruction = "echo";
		}else if(instruction.equals("echo")){
			logger.info("ECHO");
			decision = drone.scanForward();
			instruction = "fly";
		}else{
			JSONObject lastHistory = history.getLast();
			logger.info("History, {}", lastHistory);
			JSONObject information = lastHistory.getJSONObject("extras");
			logger.info("Information, {}", information);
			int distance = information.getInt("range");
			logger.info("distance", information);
			if(distance == 0){
				decision.put("action","stop");
			}else{
				decision = drone.flyForwards();
			}
			instruction = "scan";
		}
		return decision;
	}

	public JSONObject getHeading(Drone drone, int count) {
		JSONObject decision = new JSONObject();
		if(count == 0){
			decision = drone.scanLeft();
		}else if(count == 1){
			decision = drone.scanForward();
		}else{
			decision = drone.scanRight();
		}
		return decision;
	}
	
}
