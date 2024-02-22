package ca.mcmaster.se2aa4.island.team103;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IslandLocator {

	private final Logger logger = LogManager.getLogger();
	private Action next_move;
	private Action last_move;

	public JSONObject locate(Drone drone, ResponseHistory history, String starting_location, Direction start_heading, int counter) {
		/* locates the island by travelling in a diagonal, then beelining for the first echo that returns "GROUND" 
		 * Returns a JSONObject where the key "decision" contains the action command, and "result" contains "action-required" if
		 * an action command is being passed in "decision", and will contain "arrived" when the drone has reached the island.
		 * If "result" contains "arrived", the ouput JSONObject will NOT contain the key "decision"
		*/
		JSONObject decision = new JSONObject();
		JSONObject output = new JSONObject();
		if(counter == 0) {
			switch(starting_location) {
				case "NW":
					if (start_heading == Direction.EAST) {
						decision = drone.flyForwards();
					} else if (start_heading == Direction.SOUTH){
						decision = drone.turnLeft();
					}
					next_move = Action.TRIGHT;
					break;
					
				case "NE":
					if (start_heading == Direction.WEST) {
						decision = drone.flyForwards();
					} else if (start_heading == Direction.SOUTH) {
						decision = drone.turnRight();
					}
					next_move = Action.TLEFT;
					break;

				case "SW":
					if (start_heading == Direction.EAST) {
						decision = drone.flyForwards();
					} else if (start_heading == Direction.NORTH) {
						decision = drone.turnRight();
					}
					next_move = Action.TLEFT;
					break;
				
				case "SE":
					if (start_heading == Direction.WEST) {
						decision = drone.flyForwards();
					} else if (start_heading == Direction.NORTH) {
						decision = drone.turnLeft();
					}
					next_move = Action.TRIGHT;
					break;
			}
			output.put("decision", decision);
			output.put("result", "action-required");	

		} else {

			JSONObject last_result = history.getLast();
			JSONObject echo_result;

			switch(next_move) {

				case Action.TRIGHT:
					echo_result = last_result.getJSONObject("extras");
					logger.info(echo_result.toString(2));
					logger.info("Has found: {}", echo_result.has("found"));
					if (echo_result.has("found")){
						logger.info("Found (looking for GROUND): {}", echo_result.getString("found"));
						logger.info("Is Ground: {}", echo_result.getString("found").equals("GROUND"));
						if (echo_result.getString("found").equals("GROUND")) {
							logger.info("Island Spotted. Flying towards it.");
							if(echo_result.getInt("range") == 0) {
								logger.info("Arrived at Island");
								output.put("result", "arrived");
								break;
							}
							decision = drone.flyForwards();
							output.put("decision", decision);
							output.put("result", "action-required");
							next_move = Action.SCAN;
							last_move = Action.FORWARD;
							break;
						}
					}
					decision = drone.turnRight();
					output.put("decision", decision);
					output.put("result", "action-required");
					next_move = Action.FORWARD;
					last_move = Action.TRIGHT;
					break;

				case Action.TLEFT:
					echo_result = last_result.getJSONObject("extras");
					logger.info(echo_result.toString(2));
					logger.info("Has found: {}", echo_result.has("found"));
					if (echo_result.has("found")){
						logger.info("Found (looking for GROUND): {}", echo_result.getString("found"));
						logger.info("Is Ground: {}", echo_result.getString("found").equals("GROUND"));
						if (echo_result.getString("found").equals("GROUND")) {
							logger.info("Island Spotted. Flying towards it.");
							if(echo_result.getInt("range") == 0) {
								logger.info("Arrived at Island");
								output.put("result", "arrived");
								break;
							}
							decision = drone.flyForwards();
							output.put("decision", decision);
							output.put("result", "action-required");
							next_move = Action.SCAN;
							last_move = Action.FORWARD;
							break;
						}
					}
					decision = drone.turnLeft();
					output.put("decision", decision);
					output.put("result", "action-required");
					next_move = Action.FORWARD;
					last_move = Action.TLEFT;
					break;

				case Action.SCAN:
					decision = drone.scan();
					output.put("decision", decision);
					output.put("result", "action-required");
					next_move = Action.ECHO;
					break;

				case Action.ECHO:
					decision = drone.scanForward();
					output.put("decision", decision);
					output.put("result", "action-required");
					if (last_move == Action.TRIGHT) {
						next_move = Action.TLEFT;
					} else if (last_move == Action.TLEFT) {
						next_move = Action.TRIGHT;
					} else {
						next_move = Action.FORWARD;
					}
					break;

				case Action.FORWARD:
					echo_result = last_result.getJSONObject("extras");
					if (last_move == Action.FORWARD) {
						if(echo_result.getInt("range") == 0) {
							logger.info("Arrived at Island");
							output.put("result", "arrived");
							break;
						}
					}
					decision = drone.flyForwards();
					output.put("decision", decision);
					output.put("result", "action-required");
					//last_move = Action.FORWARD;
					next_move = Action.SCAN;
			}
		}
		return output;
	}

	public JSONObject getStartingLocation(Drone drone, int count, ResponseHistory memory, Direction start_heading) {
		/* Gets the starting corner of the drone. 
		 * Performs scans, returning the scan command in the "decision" key in the returned JSON.
		 * If a scan command is being issued, the "position" key will be set to "action-required".
		 * Once the position has been determined there will be no "decision" key, and "position" key will be set to the starting corner.
		 */
		JSONObject decision = new JSONObject();
		if(count == 0){
			decision.put("decision", drone.scanLeft());
			decision.put("position", "action-required");
			logger.info("[getStartingLocation] Issuing scanLeft command");
		}else if(count == 1){
			decision.put("decision", drone.scanForward());
			decision.put("position", "action-required");
			logger.info("[getStartingLocation] Issuing scanForward command");
		}else if(count == 2) {
			decision.put("decision", drone.scanRight());
			decision.put("position", "action-required");
			logger.info("[getStartingLocation] Issuing scanRight command");
		} else {
			List<JSONObject> scans = memory.getItems(-3);
			int left_scan = scans.get(0).getJSONObject("extras").getInt("range");
			int forward_scan = scans.get(1).getJSONObject("extras").getInt("range");
			int right_scan = scans.get(2).getJSONObject("extras").getInt("range");
			boolean left_wall = (left_scan == 0);
			boolean forward_wall = (forward_scan == 0);
			boolean right_wall = (right_scan == 0);

			switch(start_heading) {
				case NORTH:
					if (forward_wall & left_wall) {
						decision.put("position", "NW");
					} else if (forward_wall & right_wall) {
						decision.put("position", "NE");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "SW");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "SE");
					}
					break;
				case SOUTH:
					if (forward_wall & left_wall) {
						decision.put("position", "SE");
					} else if (forward_wall & right_wall) {
						decision.put("position", "SW");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "NE");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "NW");
					}
					break;
				case EAST:
					if (forward_wall & left_wall) {
						decision.put("position", "NE");
					} else if (forward_wall & right_wall) {
						decision.put("position", "SE");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "NW");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "SW");
					}
					break;
				case WEST:
					if (forward_wall & left_wall) {
						decision.put("position", "SW");
					} else if (forward_wall & right_wall) {
						decision.put("position", "NW");
					} else if (!forward_wall & left_wall) {
						decision.put("position", "SE");
					} else if (!forward_wall & right_wall) {
						decision.put("position", "NE");
					}
					break;
			}
			String pos = decision.getString("position");
			logger.info("Starting Location Determined: {}", pos);
			
		}
		
		return decision;
	}
	
}
