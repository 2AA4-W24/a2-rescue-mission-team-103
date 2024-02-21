package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private ResponseHistory respHistory = new ResponseHistory();
	private navHistory navHistory = new navHistory();
	private String status = "unknown";
	private String coast_status = "scanning-1";
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

		// *** THIS PORTION IS CURRENTLY BROKEN. I DIDN'T HAVE TIME TO FIX IT, BUT WILL ON THE 21ST *** //
		if(status.equals("find-island")){
			JSONObject output = islandLocator.locate(drone,respHistory, start_location, start_heading, counter);
			counter++;
			if(output.getString("result") == "action-required" ) {
				decision = output.getJSONObject("decision");
			} else {
				logger.info("Island found, stopping.");
				decision.put("action", "stop");
			}
		}

		if(status.equals("find-coast")){
			JSONObject output = coastlineMapper.coastlineScan(drone, coast_status, respHistory, navHistory );
			counter++;
			if(output.getString("result") == "action-required" ) {
				decision = output.getJSONObject("decision");
			} else {
				logger.info("Island found, stopping.");
				decision.put("action", "stop");
			}
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
