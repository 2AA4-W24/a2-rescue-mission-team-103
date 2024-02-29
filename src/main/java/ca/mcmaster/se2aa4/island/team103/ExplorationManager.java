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
	private int coast_status = 1;
	private IslandLocator islandLocator = new IslandLocator();
	private IslandRecon islandMapper = new IslandRecon();
	private Drone drone;
	private int counter = 0;
	private Direction start_heading;
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
		
		if(status.equals("find-island")){
			Optional<JSONObject> output = islandLocator.locate(drone, respHistory, start_heading);
			counter++;
			if(output.isPresent()) {
				decision = output.get();
			} else {
				logger.info("Island found, moving on.");
				status = "find-coast";
				counter = 0;
			}
		}

		if(status.equals("find-coast")){
			JSONObject output = islandMapper.islandScan(drone, respHistory);
			logger.info("OUTPUT: {}",output);
			counter++;
			if(counter > 400){
				decision.put("action","stop");
			}else{
				decision = output;
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
