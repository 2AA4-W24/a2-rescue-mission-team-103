package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Optional;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private ResponseHistory respHistory = new ResponseHistory();
	private NavHistory navHistory = new NavHistory();
	private String status = "find-island";
	private IslandLocator islandLocator = new IslandLocator();
	private IslandRecon islandMapper = new IslandRecon();
	private Drone drone;
	private int counter = 0;
	private Direction start_heading;

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
		drone = new Drone(start_heading, battery_start_level);
		navHistory.addItem(new Coordinate(0,0));
	}
	
	public JSONObject getDecision() {
		JSONObject decision = new JSONObject();
		
		if(status.equals("find-island")){
			Optional<JSONObject> output = islandLocator.locate(drone, respHistory, start_heading);
			if(output.isPresent()) {
				decision = output.get();
			} else {
				logger.info("Island found, moving on.");
				status = "find-coast";
			}
		}

		if(status.equals("find-coast")){
			/* 
			JSONObject output = islandMapper.islandScan(drone, respHistory);
			logger.info("OUTPUT: {}",output);
			if(output.has("over")){
				if(output.getString("over").equals("true")){
					logger.info("Island scan completed, moving on.");
					decision.put("action","stop");
				}
			}else{
				logger.info(output.has("response"));
				decision = output.getJSONObject("response");
				logger.info("Response: {}",decision);
			}
			*/
			JSONObject output = islandMapper.islandScan(drone, respHistory);
			counter++;
			if(output.has("over") || counter > 300){
				if(output.getString("over").equals("true") || counter > 300){
					logger.info("Island scan completed, moving on.");
					decision = drone.stop();
				}
			}else{
				decision = output.getJSONObject("response");
			}
			
			
		}

        return decision;
	}

	public void addInfo(JSONObject response){
		respHistory.addItem(response);
		drone.logCost(response.getInt("cost"));
	}

	public JSONObject getLastInfo(){
		return respHistory.getLast();
	}
}
