package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private ResponseHistory respHistory = new ResponseHistory();
	private NavHistory navHistory = new NavHistory();
	private String status = "unknown";
	private int coast_status = 1;
	private IslandLocator islandLocator = new IslandLocator();
	private CoastlineRecon coastlineMapper = new CoastlineRecon();
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
		navHistory.addItem(new Coordinate(0,0));
	}
	
	public JSONObject getDecision() {
		JSONObject decision = new JSONObject();
		
		if(status.equals("unknown")){

			JSONObject location = islandLocator.getStartingLocation(drone, counter, respHistory, start_heading);

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
			JSONObject output = islandLocator.locate(drone, respHistory, start_location, start_heading, counter);
			counter++;
			if(output.getString("result") == "action-required" ) {
				decision = output.getJSONObject("decision");
			} else {
				logger.info("Island found, moving on.");
				status = "find-coast";
			}
		}

		if(status.equals("find-coast")){
			JSONObject output = coastlineMapper.coastlineScan(drone, coast_status, respHistory, navHistory);
			logger.info("OUTPUT: {}",output);
			counter++;
			if(output.getString("result") == "action-required" ) {
				decision = output.getJSONObject("decision");
			} else {
				logger.info("Completed island coast finding, stopping mission.");
				decision.put("action","stop");
			}
			logger.info("Last coordinate {}", navHistory.getLast().toString());
		}

        return decision;
	}

	public void addInfo(JSONObject j){
		respHistory.addItem(j);
	}

	public JSONObject getLastInfo(){
		return respHistory.getLast();
	}
}
