package ca.mcmaster.se2aa4.island.team103;

import java.util.List;
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
		UTURN_F,
		UTURN_R,
		UTURN_L
	}

	private final Logger logger = LogManager.getLogger();
	private Action next_action;
	private Phase phase = Phase.SEARCH;
	private JSONObject last_result;
	private String last_echo_found;
	private int last_echo_dis;

	
	public Optional<JSONObject> locate(Drone drone, ResponseHistory history, Direction start_heading) {
		/* locates the island by travelling in a diagonal, then beelining for the first echo that returns "GROUND" 
		 * Returns a JSONObject where the key "decision" contains the action command, and "result" contains "action-required" if
		 * an action command is being passed in "decision", and will contain "arrived" when the drone has reached the island.
		 * If "result" contains "arrived", the ouput JSONObject will NOT contain the key "decision"
		*/
		JSONObject decision = new JSONObject();

		switch (phase) {
			case Phase.SEARCH:
				switch (next_action) {
					case Action.ECHO_RIGHT:
						decision = drone.scanRight();
						next_action = Action.ECHO_FORWARD;
						break;
					case Action.ECHO_FORWARD:
						decision = drone.scanForward();
						next_action = Action.ECHO_LEFT;
						last_result = history.getLast();
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						if(last_echo_found.equals("GROUND")) {
							phase = Phase.UTURN_F;
							decision = drone.turnRight();
						}
						break;
					case Action.ECHO_LEFT:
						next_action = Action.FORWARD;
						last_result = history.getLast();
						last_echo_found = last_result.getJSONObject("extras").getString("found");
						last_echo_dis = last_result.getJSONObject("extras").getInt("range");
						if(last_echo_found.equals("GROUND") && last_echo_dis == 2) {
							phase = Phase.UTURN_F;
							decision = drone.flyForwards();
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
				}
				break;
			case Phase.UTURN_F:
				break;
			case Phase.UTURN_R:
				break;
			case Phase.UTURN_L:
				break;

		}
	}
	
}
