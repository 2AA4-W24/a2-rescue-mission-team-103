package ca.mcmaster.se2aa4.island.team103;
import ca.mcmaster.se2aa4.island.team103.DroneCommands.*;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

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
	private Drone drone;
	private History<JSONObject> history;
	private JSONObject last_result;
	private String last_echo_found;
	private Counter stage = new Counter();
	private CommandHandler commander = new CommandHandler();
	private Command shiftRight;
	private Command shiftLeft;
	private Command Uturn;
	private int last_echo_dist;
	private int dist;
	

	public IslandLocator(Drone drone_in, History<JSONObject> history_in) {
		this.drone = drone_in;
		this.history = history_in;
		this.shiftRight = new ShiftRight(drone_in);
		this.shiftLeft = new ShiftLeft(drone_in);
		this.Uturn = new UturnLeft(drone_in);
	}
	
	public Optional<JSONObject> nextAction(Drone temp_placeholder, History<JSONObject> temp_placeholder1) {
		/* */
		JSONObject decision = new JSONObject();
		Optional<JSONObject> result;
			
		if (phase == Phase.SEARCH) {
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
						decision = drone.turnRight();
						phase = Phase.UTURN_R;
						logger.info("Exiting Search Phase -> UTURN_R");
						logger.info("Heading: {}", drone.getHeading());
						logger.info("Decision: {}", decision.toString());
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
		}

		else if (phase == Phase.TRAVEL_TO_END) {
			switch (this.stage.value()) {
				case 0:
					decision = drone.echoForward();
					break;
				case 1:
					last_result = history.getLast();
					dist = last_result.getJSONObject("extras").getInt("range");
					if(dist <= 1) {
						phase = Phase.UTURN_F;
						logger.info("Exiting TRAVEL_TO_END -> UTURN_F, dist <= 1");
						this.stage.reset();
						decision = drone.scan();
					} else {
						decision = drone.flyForwards();
					}
					break;
				default:
					if(this.stage.value() < dist - 1) {
						decision = drone.flyForwards();
					} else {
						decision = drone.flyForwards();
						phase = Phase.UTURN_F;
						logger.info("Exiting TRAVEL_TO_END -> UTURN_F");
						this.stage.reset();
					}
					break;
			}
			this.stage.next();
		} else {
			
			if (phase == Phase.UTURN_F) {
				commander.setCommand(this.Uturn);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					phase = Phase.FINAL_FRWD;
					logger.info("Exiting UTURN_F -> FINAL_FRWD");
				}
			}

			if (phase == Phase.UTURN_R) {
				commander.setCommand(this.shiftRight);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					phase = Phase.FINAL_FRWD;
					logger.info("Exiting UTURN_R -> FINAL_FRWD");
				}
			}

			if (phase == Phase.UTURN_L) {
				commander.setCommand(this.shiftLeft);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					phase = Phase.FINAL_FRWD;
					logger.info("Exiting UTURN_L -> FINAL_FRWD");
				}
			}
				
			if (phase == Phase.FINAL_FRWD) {
				switch (this.stage.value()) {
					case 0:
						decision = drone.echoForward();
						break;
					case 1:
						last_result = history.getLast();
						dist = last_result.getJSONObject("extras").getInt("range");
						if(dist == 0) {
							logger.info("Exiting FINAL_FRWD -> Returning empty (dist == 0 immediately after uturn)");
							this.stage.reset();
							return Optional.empty();
						} else {
							decision = drone.flyForwards();
						}
						break;
					default:
						if(this.stage.value() < dist){
							decision = drone.flyForwards();
						} else {
							logger.info("Exiting FINAL_FRWD -> Returning empty");
							this.stage.reset();
							return Optional.empty();
						}
						break;
				}
				this.stage.next();
			}
		}
		return Optional.of(decision);
	}
}
