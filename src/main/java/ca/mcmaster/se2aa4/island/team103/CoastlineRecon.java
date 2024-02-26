package ca.mcmaster.se2aa4.island.team103;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class CoastlineRecon {
	/* Intended to scan all coastline of the island, get an idea of the size. This 
	 * result will be stored in the map, and used for emergency site and inlet finding. 
	*/
	private final Logger logger = LogManager.getLogger();
	private Action next_move = Action.ECHO_RIGHT;
	private Action last_move;

	public JSONObject coastlineScan(Drone drone, int counter, ResponseHistory respHistory, NavHistory navHistory){

		JSONObject decision = new JSONObject();
		JSONObject output = new JSONObject();
		Coordinate new_add;

		if(navHistory.getFirst() == navHistory.getLast() && navHistory.getSize()>1){
			output.put("result","complete");
		}else{
			switch(next_move) {
				case Action.ECHO_RIGHT:

					decision = drone.scanRight();
					output.put("decision",decision);
					output.put("result","action-required");

					next_move = Action.ECHO_FORWARD;
					last_move = Action.ECHO_RIGHT;
					break;

				case Action.ECHO_FORWARD:

					decision = drone.scanForward();
					output.put("decision",decision);
					output.put("result","action-required");

					next_move = Action.ECHO_LEFT;
					last_move = Action.ECHO_FORWARD;
					break;

				case Action.ECHO_LEFT:

					decision = drone.scanLeft();
					output.put("decision",decision);
					output.put("result","action-required");

					next_move = Action.SCAN;
					last_move = Action.ECHO_RIGHT;
					break;

				case Action.SCAN:

					decision = drone.scan();
					output.put("decision",decision);
					output.put("result","action-required");
					List<JSONObject> scans = respHistory.getItems(-3);
					logger.info("Scans, {}", scans);
					JSONObject echo_right = scans.get(scans.size()-3);
					JSONObject echo_straight = scans.get(scans.size()-2);
					JSONObject echo_left = scans.get(scans.size()-1);

					String result_right = echo_right.getJSONObject("extras").getString("found");
					int right_range = echo_right.getJSONObject("extras").getInt("range");

					String result_straight = echo_straight.getJSONObject("extras").getString("found");
					int straight_range = echo_straight.getJSONObject("extras").getInt("range");

					String result_left = echo_left.getJSONObject("extras").getString("found");
					int left_range = echo_left.getJSONObject("extras").getInt("range");

					logger.info("Result_Right: {}", result_right);
					logger.info("Right_Range: {}", right_range);
					logger.info("Result_Straight: {}", result_straight);
					logger.info("Straight_Range: {}", straight_range);
					logger.info("Result_Left: {}", result_left);
					logger.info("Left_Range: {}", left_range);

					if(result_left.equals("GROUND") && result_straight.equals("GROUND")){
						next_move = Action.TRIGHT;
					}else if(result_left.equals("GROUND")){
						next_move = Action.FORWARD;
					}else{
						next_move = Action.TLEFT;
					}
					break;

				case Action.TRIGHT:
					decision = drone.turnRight();
					output.put("decision",decision);
					output.put("result","action-required");

					new_add = addToHistory(drone,"right",navHistory.getLast());
					output.put("coordinateX",new_add.x());
					output.put("coordinateY",new_add.y());

					next_move = Action.ECHO_RIGHT;
					last_move = Action.TRIGHT;
					break;

				case Action.FORWARD:
					decision = drone.flyForwards();
					output.put("decision",decision);
					output.put("result","action-required");

					new_add = addToHistory(drone,"straight",navHistory.getLast());
					output.put("coordinateX",new_add.x());
					output.put("coordinateY",new_add.y());

					logger.info("Going forwards, coordinates are: {}",new_add.toString());
					next_move = Action.ECHO_RIGHT;
					last_move = Action.FORWARD;
					break;

				case Action.TLEFT:
					decision = drone.turnLeft();
					output.put("decision",decision);
					output.put("result","action-required");

					new_add = addToHistory(drone,"left",navHistory.getLast());
					output.put("coordinateX",new_add.x());
					output.put("coordinateY",new_add.y());

					logger.info("Going left, coordinates are: {}",new_add.toString());
					next_move = Action.ECHO_RIGHT;
					last_move = Action.TLEFT;
					break;

				default:
					break;
			}
		}
		return output;
	}

	public Coordinate addToHistory(Drone drone, String turning, Coordinate prev){
		/*Method used to add coordinate to navigation history, depending on turn and current heading.
		 * For example, if going north and turning left, new coordinate will be currentX-1,currentY-1
		*/
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
		}else if(turning.equals("right")){
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
		}else{ // Going forward otherwise
			switch(heading){
				case NORTH:
					return new Coordinate(prev.x(),prev.y()-1);
				case EAST:
					return new Coordinate(prev.x()+1,prev.y());
				case WEST:
					return new Coordinate(prev.x()-1,prev.y());
				case SOUTH:
					return new Coordinate(prev.x(),prev.y()+1);
				default:
					return new Coordinate(prev.x(),prev.y());
			}
		}
	}
}
