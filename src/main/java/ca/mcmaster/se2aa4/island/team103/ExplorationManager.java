package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Optional;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private History<JSONObject> respHistory = new ResponseHistory();
	private NavHistory navHistory = new NavHistory();
	private String status = "find-island";
	private DroneController islandLocator = new IslandLocator();
	private DroneController islandMapper = new IslandRecon();
	private Drone drone;
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
			Optional<JSONObject> output = islandLocator.nextAction(drone, respHistory);
			if(output.isPresent()) {
				decision = output.get();
			} else {
				logger.info("Island found, moving on.");
				status = "find-coast";
			}
		}

		if(status.equals("find-coast")){
			Optional<JSONObject> output = islandMapper.nextAction(drone, respHistory);
			logger.info("OUTPUT: {}",output);
			if(output.isPresent()){
				decision = output.get();
			}else{
				decision = drone.stop();
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
