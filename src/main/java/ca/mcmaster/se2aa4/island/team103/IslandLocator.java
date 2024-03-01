package ca.mcmaster.se2aa4.island.team103;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IslandLocator {

	enum Phase {
		SEARCH,
		TRAVEL_F,
		TRAVEL_R,
		TRAVEL_L,
		TRAVEL_TO_END,
		UTURN_F,
		UTURN_R,
		UTURN_L,
		FINAL_FRWD
	}

	private final Logger logger = LogManager.getLogger();
	private Action next_action = Action.ECHO_RIGHT;
	private Phase phase = Phase.SEARCH;
	private JSONObject last_result;
	private String last_echo_found;
	private int last_echo_dist;
	private int uturn_stage = 0;
	private int dist;
	private int trvl_to_end_count = 0;

	
	public Optional<JSONObject> locate(Drone drone, ResponseHistory history, Direction start_heading) {
		/* */
		JSONObject decision = new JSONObject();
		switch (phase) {
			
			case Phase.SEARCH:
				switch (next_action) {
					case Action.ECHO_RIGHT:
						decision = drone.scanRight();
						next_action = Action.ECHO_FORWARD;
						break;
					case Action.ECHO_FORWARD:
						last_result = history.getLast();
						next_action = Action.ECHO_LEFT;
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						if(last_echo_found.equals("GROUND")) {
							phase = Phase.UTURN_F;
							decision = drone.turnRight();
						} else {
							decision = drone.scanForward();
						}
						break;
					case Action.ECHO_LEFT:
						next_action = Action.FORWARD;
						last_result = history.getLast();
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						last_echo_dist = last_result.getJSONObject("extras").getInt("range");
						if(last_echo_found.equals("GROUND") && last_echo_dist == 2) {
							phase = Phase.UTURN_F;
							decision = drone.turnRight();
						} else {
							decision = drone.scanLeft();
						}
						break;
					case Action.FORWARD:
						next_action = Action.ECHO_RIGHT;
						last_result = history.getLast();
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						if(last_echo_found.equals("GROUND")) {
							phase = Phase.UTURN_L;
							decision = drone.turnLeft();
						} else {
							decision = drone.flyForwards();
						}
						break;
					default:
						logger.error("next_action is not in an acceptable state");
						
				}
				break;
			case Phase.TRAVEL_TO_END:
				switch (trvl_to_end_count) {
					case 0:
						decision = drone.scanForward();
						break;
					case 1:
						last_result = history.getLast();
						dist = last_result.getJSONObject("extras").getInt("range");
						if(dist <= 1) {
							phase = Phase.UTURN_F;
							decision = drone.scan();
						} else {
							decision = drone.flyForwards();
						}
						break;
					default:
						if(trvl_to_end_count < dist - 2) {
							decision = drone.flyForwards();
						} else {
							decision = drone.flyForwards();
							phase = Phase.UTURN_F;
						}
						break;
				}
				trvl_to_end_count++;
				break;
			case Phase.UTURN_F:
				switch (uturn_stage) {
					case 0:
						decision = drone.turnLeft();
						break;
					case 1:
						decision = drone.turnLeft();
						phase = Phase.FINAL_FRWD;
						break;
				}
				break;
			case Phase.UTURN_R:
				switch (uturn_stage) {
					case 0 | 1:
						decision = drone.turnRight();
						break;
					case 2:
						decision = drone.flyForwards();
						break;
					case 3 | 4:
						decision = drone.turnRight();
						break;
					case 5:
						decision = drone.turnRight();
						phase = Phase.FINAL_FRWD;
						break;
				}
				uturn_stage++;
				break;
			case Phase.UTURN_L:
				switch (uturn_stage) {
					case 0 | 1:
						decision = drone.turnLeft();
						break;
					case 2:
						decision = drone.flyForwards();
						break;
					case 3 | 4:
						decision = drone.turnLeft();
						break;
					case 5:
						decision = drone.turnLeft();
						phase = Phase.FINAL_FRWD;
						break;
				}
				uturn_stage++;
				break;
			case Phase.FINAL_FRWD:
				switch (trvl_to_end_count) {
					case 0:
						decision = drone.scanForward();
						break;
					case 1:
						last_result = history.getLast();
						dist = last_result.getJSONObject("extras").getInt("range");
						if(dist == 0) {
							phase = Phase.UTURN_F;
							decision = drone.scan();
						} else {
							decision = drone.flyForwards();
						}
						break;
					default:
						if(trvl_to_end_count < dist) {
							decision = drone.flyForwards();
						} else {
							return Optional.empty();
						}
						break;
				}
				trvl_to_end_count++;
				break;
				
			default:
				logger.error("phase is not in an acceptable state");

		}
		return Optional.of(decision);
	}
	
}
