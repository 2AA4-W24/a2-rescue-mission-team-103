package ca.mcmaster.se2aa4.island.team103;

import java.util.List;

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

	public JSONObject getStartingLocation(Drone drone, int count, ResponseHistory memory, Direction start_heading) {
		/* Gets the starting corner of the drone. 
		 * Performs scans, returning the scan command in the "decision" key in the returned JSON.
		 * If a scan command is being issued, the "position" key will be set to "action-required".
		 * Once the position has been determined there will be no "decision" key, and "position" key will be set to the starting corner.
		 */
		JSONObject decision = new JSONObject();
		if(count == 0){
			decision.put("decision", drone.scanLeft());
			decision.put("position", "action-required");
			logger.info("[getStartingLocation] Issuing scanLeft command");
		}else if(count == 1){
			decision.put("decision", drone.scanForward());
			decision.put("position", "action-required");
			logger.info("[getStartingLocation] Issuing scanForward command");
		}else if(count == 2) {
			decision.put("decision", drone.scanRight());
			decision.put("position", "action-required");
			logger.info("[getStartingLocation] Issuing scanRight command");
		} else {
			List<JSONObject> scans = memory.getItems(-3);
			int left_scan = scans.get(0).getJSONObject("extras").getInt("range");
			int forward_scan = scans.get(1).getJSONObject("extras").getInt("range");
			int right_scan = scans.get(2).getJSONObject("extras").getInt("range");
			boolean left_wall;
			boolean forward_wall;
			boolean right_wall;

			if (left_scan == 0) {
				left_wall = true;
			} else {
				left_wall = false;
			}

			if (right_scan == 0) {
				right_wall = true;
			} else {
				right_wall = false;
			}

			if (forward_scan == 0) {
				forward_wall = true;
			} else {
				forward_wall = false;
			}

			switch(start_heading) {
				case NORTH:
					if (forward_wall & left_wall) {
						decision.put("position", "NW");
					} else if (forward_wall & right_wall) {
						decision.put("position", "NE");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "SW");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "SE");
					}
					break;
				case SOUTH:
					if (forward_wall & left_wall) {
						decision.put("position", "SE");
					} else if (forward_wall & right_wall) {
						decision.put("position", "SW");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "NE");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "NW");
					}
					break;
				case EAST:
					if (forward_wall & left_wall) {
						decision.put("position", "NE");
					} else if (forward_wall & right_wall) {
						decision.put("position", "SE");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "NW");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "SW");
					}
					break;
				case WEST:
					if (forward_wall & left_wall) {
						decision.put("position", "SW");
					} else if (forward_wall & right_wall) {
						decision.put("position", "NW");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "SE");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "NE");
					}
					break;
			}
			String pos = decision.getString("position");
			logger.info("Starting Location Determined: {}", pos);
			
		}
		
		return decision;
	}
	
}
