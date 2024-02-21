package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class CoastlineRecon {
	/* Intended to scan all coastline of the island, get an idea of the size. This 
	 * result will be stored in the map, and used for emergency site and inlet finding. 
	*/
	public JSONObject coastlineScan(Drone drone, String status, ResponseHistory history, CoordinateHistory navHistory){
		JSONObject decision = new JSONObject();
		switch(status){
			case "scanning-1":
				decision = drone.scanForward();
				break;
			case "left":
				// If scan forward returns land, turn left
				decision = drone.turnLeft();
			case "scanning-2":
				// If scan forward returns no land, scan right
				decision = drone.scanRight();
				break;
			case "straight":
				// If scan right returns land, then fly straight
				decision = drone.flyForwards();
			case "right":
				// If scan right returns no land, then turn right
				decision = drone.turnRight();
		}
		// Keep doing this until starting coordinate is once again found.
		return decision;
	}
}
