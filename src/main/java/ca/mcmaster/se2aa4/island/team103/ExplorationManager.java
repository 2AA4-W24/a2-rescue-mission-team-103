package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.drone.Direction;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.drone.DroneController;
import ca.mcmaster.se2aa4.island.team103.history.Coordinate;
import ca.mcmaster.se2aa4.island.team103.history.History;
import ca.mcmaster.se2aa4.island.team103.history.ResponseHistory;
import ca.mcmaster.se2aa4.island.team103.islandLocating.IslandLocator;
import ca.mcmaster.se2aa4.island.team103.islandScanning.IslandScanner;
import ca.mcmaster.se2aa4.island.team103.siteTracking.SiteTracker;

import java.util.Optional;
import java.util.List;

public class ExplorationManager {

	private final static Logger logger = LogManager.getLogger();

	private History<JSONObject> respHistory = new ResponseHistory();
	private DroneController islandLocator;
	private DroneController islandMapper;
	private Drone drone;
	private Direction start_heading;

	private enum ExplorationPhase{
		FINDISLAND,
		SCANISLAND
	}
	
	private ExplorationPhase status = ExplorationPhase.FINDISLAND;

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
	
	// Primary method for getting drone decision. Either in island finding phase, or
	public JSONObject getDecision() {
		JSONObject decision = new JSONObject();

		if(status.equals(ExplorationPhase.FINDISLAND)){
			Optional<JSONObject> output = islandLocator.nextAction();
			if(output.isPresent()) {
				decision = output.get();
			} else {
				logger.info("Island found, moving on.");
				status = ExplorationPhase.SCANISLAND;
			}
		}

		if(status.equals(ExplorationPhase.SCANISLAND)){
			Optional<JSONObject> output = islandMapper.nextAction();
			if(output.isPresent()){
				decision = output.get();
			}else{
				logger.info("Island scanning complete, moving on.");
				decision = drone.stop();
			}
		}
        return decision;
	}

	// Getting site and inlet coordinates, and returning closest creek found.
	public String getFinalReport() {
		SiteTracker tracker = new SiteTracker();
		tracker.compilePointsOfInterest(getResponseReport(), getNavReport());
		return tracker.getClosestInlet();
	}

	// Associated methods for getting history (both response and navigation)
	public void addInfo(JSONObject response){
		respHistory.addItem(response);
		drone.logCost(response.getInt("cost"));
	}

	private List<JSONObject> getResponseReport(){
		return respHistory.getItems(0);
	}

	private List<Coordinate> getNavReport(){
		return drone.getNavHistory();
	}
}
