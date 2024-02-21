package ca.mcmaster.se2aa4.island.team103;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class CoastlineRecon {
	/* Intended to scan all coastline of the island, get an idea of the size. This 
	 * result will be stored in the map, and used for emergency site and inlet finding. 
	*/
	public JSONObject coastlineScan(Drone drone, String status, ResponseHistory history, navHistory navHistory){
		JSONObject decision = new JSONObject();
		switch(status){
			case "scanning-1":
				decision = drone.scanForward();
				break;
			case "left":
				// If scan forward returns land, turn left
				addToHistory(drone,"left",navHistory.getLast());
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
				addToHistory(drone,"right",navHistory.getLast());
				decision = drone.turnRight();
		}
		// Keep doing this until starting coordinate is once again found.
		return decision;
	}

	public Coordinate addToHistory(Drone drone, String turning, Coordinate prev){
		/*Method used to add coordinate to navigation history, depending on turn and current heading.*/
		Direction heading = drone.getHeading();
		if(turning.equals("left")){
			switch(heading){
				case NORTH:
					return new Coordinate(prev.x()-1,prev.y()-1);
				case EAST:
					return new Coordinate(prev.x()+1,prev.y()-1);
				case WEST:
					return new Coordinate(prev.x()-1,prev.y()+1);
				case SOUTH:
					return new Coordinate(prev.x()+1,prev.y()+1);
				default:
					return new Coordinate(prev.x(),prev.y());
			}
		}else{
			switch(heading){
				case NORTH:
					return new Coordinate(prev.x()+1,prev.y()-1);
				case EAST:
					return new Coordinate(prev.x()+1,prev.y()+1);
				case WEST:
					return new Coordinate(prev.x()-1,prev.y()-1);
				case SOUTH:
					return new Coordinate(prev.x()-1,prev.y()+1);
				default:
					return new Coordinate(prev.x(),prev.y());
			}
		}
	}
}
