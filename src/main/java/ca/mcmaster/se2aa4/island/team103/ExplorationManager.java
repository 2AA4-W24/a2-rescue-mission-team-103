package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ExplorationManager {

	private final Logger logger = LogManager.getLogger();

	private ResponseHistory history = new ResponseHistory();
	private String status = "unknown";
	private IslandLocator islandLocator = new IslandLocator();
	private Drone drone = new Drone();
	private int headingCounter = 0;
	
	public JSONObject getDecision() {
		JSONObject decision= new JSONObject();
		if(status.equals("unknown")){
			decision = islandLocator.getHeading(drone,headingCounter);
			if(headingCounter == 2){
				status = "find-island";
			}else{
				headingCounter++;
			}
		}
		else if(status.equals("find-island")){
			logger.info("heading to decision method");
			decision = islandLocator.locate(drone, history);
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
