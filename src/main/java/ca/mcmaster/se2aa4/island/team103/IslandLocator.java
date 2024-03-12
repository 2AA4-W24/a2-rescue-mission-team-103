package ca.mcmaster.se2aa4.island.team103;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IslandLocator implements DroneController {

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
	private int trvl_to_isl_count = 0;	

	
	public Optional<JSONObject> nextAction(Drone drone, History<JSONObject> history) {
		/* */
		JSONObject decision = new JSONObject();
		switch (phase) {
			
			case Phase.SEARCH:
				switch (next_action) {
					case Action.ECHO_RIGHT:
						decision = drone.echoRight();
						next_action = Action.ECHO_FORWARD;
						break;
					case Action.ECHO_FORWARD:
						last_result = history.getLast();
						next_action = Action.ECHO_LEFT;
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						if(last_echo_found.equals("GROUND")) {
							phase = Phase.UTURN_R;
							logger.info("Exiting Search Phase -> UTURN_R");
							decision = drone.turnRight();
						} else {
							decision = drone.echoForward();
						}
						break;
					case Action.ECHO_LEFT:
						next_action = Action.FORWARD;
						last_result = history.getLast();
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						last_echo_dist = last_result.getJSONObject("extras").getInt("range");
						if(last_echo_found.equals("GROUND") && last_echo_dist == 2) {
							phase = Phase.TRAVEL_TO_END;
							logger.info("Exiting Search Phase -> TRAVEL_TO_END");
							decision = drone.turnRight();
						} else {
							decision = drone.echoLeft();
						}
						break;
					case Action.FORWARD:
						next_action = Action.ECHO_RIGHT;
						last_result = history.getLast();
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						if(last_echo_found.equals("GROUND")) {
							phase = Phase.UTURN_L;
							logger.info("Exiting Search Phase -> UTURN_L");
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
						decision = drone.echoForward();
						break;
					case 1:
						last_result = history.getLast();
						dist = last_result.getJSONObject("extras").getInt("range");
						if(dist <= 1) {
							phase = Phase.UTURN_F;
							logger.info("Exiting TRAVEL_TO_END -> UTURN_F, dist <= 1");
							decision = drone.scan();
						} else {
							decision = drone.flyForwards();
						}
						break;
					default:
						if(trvl_to_end_count < dist - 1) {
							decision = drone.flyForwards();
						} else {
							decision = drone.flyForwards();
							phase = Phase.UTURN_F;
							logger.info("Exiting TRAVEL_TO_END -> UTURN_F");
						}
						break;
				}
				trvl_to_end_count++;
				break;
			case Phase.UTURN_F:
				logger.info("uturn_stage: {}", uturn_stage);
				switch (uturn_stage) {
					case 0:
						decision = drone.turnLeft();
						break;
					case 1:
						decision = drone.turnLeft();
						phase = Phase.FINAL_FRWD;
						logger.info("Exiting UTURN_F -> FINAL_FRWD");
						break;
				}
				uturn_stage++;
				break;
			case Phase.UTURN_R:
				switch (uturn_stage) {
					case 0:
						decision = drone.turnRight();
						break;
					case 1:
						decision = drone.flyForwards();
						break;
					case 2:
					case 3:
						decision = drone.turnRight();
						break;
					case 4:
						decision = drone.turnRight();
						phase = Phase.FINAL_FRWD;
						logger.info("Exiting UTURN_R -> FINAL_FRWD");
						break;
				}
				uturn_stage++;
				break;
			case Phase.UTURN_L:
				switch (uturn_stage) {
					case 0:
						decision = drone.turnLeft();
						break;
					case 1:
						decision = drone.flyForwards();
						break;
					case 2:
					case 3:
						decision = drone.turnLeft();
						break;
					case 4:
						decision = drone.turnLeft();
						phase = Phase.FINAL_FRWD;
						logger.info("Exiting UTURN_L -> FINAL_FRWD");
						break;
				}
				uturn_stage++;
				break;
			case Phase.FINAL_FRWD:
				logger.info("Final frwd decision: {}", trvl_to_isl_count);
				switch (trvl_to_isl_count) {
					case 0:
						decision = drone.echoForward();
						break;
					case 1:
						last_result = history.getLast();
						dist = last_result.getJSONObject("extras").getInt("range");
						if(dist == 0) {
							logger.info("Exiting FINAL_FRWD -> Returning empty (dist == 0 immediately after uturn)");
							return Optional.empty();
						} else {
							decision = drone.flyForwards();
						}
						break;
					default:
						if(trvl_to_isl_count < dist){
							decision = drone.flyForwards();
						} else {
							logger.info("Exiting FINAL_FRWD -> Returning empty");
							return Optional.empty();
						}
						break;
				}
				trvl_to_isl_count++;
				break;
				
			default:
				logger.error("phase is not in an acceptable state");

		}
		return Optional.of(decision);
	}
	
}
