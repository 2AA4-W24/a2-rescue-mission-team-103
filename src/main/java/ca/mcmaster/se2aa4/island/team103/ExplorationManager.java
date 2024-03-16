package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Optional;
import java.util.List;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private History<JSONObject> respHistory = new ResponseHistory();
	private String status = "start";
	private DroneController islandLocator = new IslandLocator();
	private DroneController islandMapper = new IslandRecon();
	private Drone drone;
	private Direction start_heading;

	public ExplorationManager(String heading, Integer battery_start_level) {
		
		// Initializes start heading, battery level and drone
		if(heading.equals("N")) {
			start_heading = Direction.NORTH;
		} else if (heading.equals("S")) {
			start_heading = Direction.SOUTH;
		} else if (heading.equals("W")) {
			start_heading = Direction.WEST;
		} else {
			start_heading = Direction.EAST;
		}
		logger.info("Recieved start heading: {}", heading);
		logger.info("Initializing dron with heading: {}", start_heading);
		drone = new Drone(start_heading, battery_start_level);
	}
	
	public JSONObject getDecision() {
		JSONObject decision = new JSONObject();

		if(status.equals("start")) {
			decision = drone.scan();
			status = "find-island";
		}
		else if(status.equals("find-island")){
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
			if(output.isPresent()){
				decision = output.get();
			}else{
				logger.info("Island scanning complete, moving on.");
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

	public List<JSONObject> getResponseReport(){
		return respHistory.getItems(0,respHistory.getSize());
	}

	public List<Coordinate> getNavReport(){
		List<Coordinate> navHistory = drone.getNavHistory();
		return navHistory;
	}
}
