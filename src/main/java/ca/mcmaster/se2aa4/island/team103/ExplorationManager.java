package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private ResponseHistory history = new ResponseHistory();
	private String status = "unknown";
	private IslandLocator islandLocator = new IslandLocator();
	private Drone drone;
	private int counter = 0;
	private Direction start_heading;
	private String start_location;
	private Battery battery_tracker;

	public ExplorationManager(String heading, Integer battery_start_level) {
		// Initializes start heading, battery level and drone

		if(heading == "N") {
			start_heading = Direction.NORTH;
		} else if (heading == "S") {
			start_heading = Direction.SOUTH;
		} else if (heading == "W") {
			start_heading = Direction.WEST;
		} else {
			start_heading = Direction.EAST;
		}

		battery_tracker = new Battery(battery_start_level);
		drone = new Drone(start_heading);
	}
	
	public JSONObject getDecision() {
		JSONObject decision = new JSONObject();
		
		if(status.equals("unknown")){

			JSONObject location = islandLocator.getStartingLocation(drone,counter,history,start_heading);

			if(location.getString("position") == "action-required") {
				decision = location.getJSONObject("decision");
				counter++;
			} else {
				start_location = location.getString("position");
				status = "find-island";
				counter = 0;
			}
			
			
		}
		if(status.equals("find-island")){
			logger.info("Heading to decision method");
			decision = islandLocator.locate(drone, history, start_location, start_heading, counter);
		}
		
        return decision;
	}

	public void addInfo(JSONObject j){
		history.addItem(j);
	}

	public JSONObject getLastInfo(){
		return history.getLast();
	}
}
