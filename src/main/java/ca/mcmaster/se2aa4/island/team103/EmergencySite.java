package ca.mcmaster.se2aa4.island.team103;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class EmergencySite {

	private final Logger logger = LogManager.getLogger();

	public EmergencySite(Map m, Drone d){
		// Start by getting drone orientation.
		// Echo forward, left, and right (assuming no need to scan backwards initially)
		// Then, based on result can then look to travel diagonally to island
		// As example, if heading is E and longest paths are going right and forward, travel south-east to get to the island.
		
		// d.echoRight, d.echoLeft, d.echoAhead
		// pull reponses from these
		// find two max
		// drone.travel(right)
		JSONObject left = d.scanLeft();
		JSONObject right = d.scanRight();
		JSONObject forward = d.scanForward();
		logger.info("** Initialization info:\n {}",left.toString(2));
		logger.info("** Initialization info:\n {}",right.toString(2));
		logger.info("** Initialization info:\n {}",forward.toString(2));

	}
}
