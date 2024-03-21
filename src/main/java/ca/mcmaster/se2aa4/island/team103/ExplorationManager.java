package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.Optional;
import java.util.List;

public class ExplorationManager {

	private final static Logger logger = LogManager.getLogger();

	private History<JSONObject> respHistory = new ResponseHistory();
	private String status = "find-island";
	private DroneController islandLocator;
	private DroneController islandMapper;
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
		this.drone = new Drone(start_heading, battery_start_level);
		this.islandLocator = new IslandLocator(this.drone, this.respHistory);
		this.islandMapper = new IslandScanner(this.drone, this.respHistory);
		logger.info("Initializing drone with heading: {}", start_heading);
	}
	
	public JSONObject getDecision() {
		JSONObject decision = new JSONObject();

		if(status.equals("find-island")){
			Optional<JSONObject> output = islandLocator.nextAction();
			if(output.isPresent()) {
				decision = output.get();
			} else {
				logger.info("Island found, moving on.");
				status = "find-coast";
			}
		}

		if(status.equals("find-coast")){
			Optional<JSONObject> output = islandMapper.nextAction();
			if(output.isPresent()){
				logger.info("HEADING {}", drone.getHeading());
				decision = output.get();
			}else{
				logger.info("Island scanning complete, moving on.");
				decision = drone.stop();
			}
		}
        return decision;
	}

	public String getFinalReport() {
		SiteTracker tracker = new SiteTracker();
		tracker.compilePointsOfInterest(getResponseReport(), getNavReport());
		return tracker.getClosestInlet();
	}

	public void addInfo(JSONObject response){
		respHistory.addItem(response);
		drone.logCost(response.getInt("cost"));
	}

	public JSONObject getLastInfo(){
		return respHistory.getLast();
	}

	private List<JSONObject> getResponseReport(){
		return respHistory.getItems(0,respHistory.getSize());
	}

	private List<Coordinate> getNavReport(){
		return drone.getNavHistory();
	}
}
