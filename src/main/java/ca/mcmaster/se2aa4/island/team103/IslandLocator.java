package ca.mcmaster.se2aa4.island.team103;
import ca.mcmaster.se2aa4.island.team103.DroneCommands.*;
import ca.mcmaster.se2aa4.island.team103.IslandLocatorPhases.*;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.ArrayList;

public class IslandLocator implements DroneController {

	enum Phase {
		SEARCH,
		TRAVEL_TO_END,
		UTURN_L,
		TURN_R,
		TURN_L,
		FINAL_FRWD
	}

	private final Logger logger = LogManager.getLogger();
	private Action next_action = Action.ECHO;
	private Phase phase = Phase.SEARCH;
	private Drone drone;
	private History<JSONObject> history;
	private CommandHandler commander = new CommandHandler();
	private Command turnRight;
	private Command turnLeft;
	private Command Uturn;
	private Command forwardToCoast;
	private Command travelToEnd;
	private Command echoSearch;
	

	public IslandLocator(Drone drone_in, History<JSONObject> history_in) {
		this.drone = drone_in;
		this.history = history_in;
		this.turnRight = new TurnRight(this.drone);
		this.turnLeft = new TurnLeft(this.drone);
		this.Uturn = new UturnLeft(this.drone);
		this.forwardToCoast = new FinalForward(this.drone, this.history);
		this.travelToEnd = new TravelToEnd(this.drone, this.history);
		this.echoSearch = new EchoSearch(this.drone);
	}
	
	public Optional<JSONObject> nextAction() {
		/* */
		JSONObject decision = new JSONObject();
		Optional<JSONObject> result;
		List<JSONObject> echo_results;
			
		if (this.phase == Phase.SEARCH) {

			if (next_action == Action.ECHO) {
				
				commander.setCommand(echoSearch);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					next_action = Action.MOVE;
				}
			}
				
			if (next_action == Action.MOVE) {

				next_action = Action.ECHO;
				echo_results = history.getItems(-3);
				List<String> echo_found = new ArrayList<String>(3);
				List<Integer> echo_range = new ArrayList<Integer>(3);

				for (int i = 0; i < echo_results.size(); i++) {
					echo_found.add(echo_results.get(i).getJSONObject("extras").getString("found"));
					echo_range.add(echo_results.get(i).getJSONObject("extras").getInt("range"));
				}

				if (echo_found.get(0).equals("GROUND")) {
					this.phase = Phase.TURN_R;
					logger.info("Exiting Search Phase -> TURN_R");
					decision = drone.turnRight();
				}
				else if (echo_found.get(1).equals("GROUND") && echo_range.get(1) == 2) {
					this.phase = Phase.TRAVEL_TO_END;
					logger.info("Exiting Search Phase -> TRAVEL_TO_END");
					decision = drone.turnRight();
				}
				else if (echo_found.get(2).equals("GROUND")) {
					this.phase = Phase.TURN_L;
					logger.info("Exiting Search Phase -> TURN_L");
					decision = drone.turnLeft();
				}
				else {
					decision = drone.flyForwards();
				}
			}

		} else {

			if (this.phase == Phase.TRAVEL_TO_END) {
				commander.setCommand(travelToEnd);
				result = commander.nextAction();
	
				if (result.isPresent()) {
					decision = result.get();
				} else {
					this.phase = Phase.UTURN_L;
					logger.info("Exiting TRAVEL_TO_END -> UTURN_L");
				}
			}
			
			if (this.phase == Phase.UTURN_L) {
				commander.setCommand(this.Uturn);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					this.phase = Phase.FINAL_FRWD;
					logger.info("Exiting UTURN_L -> FINAL_FRWD");
				}
			}

			if (this.phase == Phase.TURN_R) {
				commander.setCommand(this.turnRight);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					this.phase = Phase.FINAL_FRWD;
					logger.info("Exiting TURN_R -> FINAL_FRWD");
				}
			}

			if (this.phase == Phase.TURN_L) {
				commander.setCommand(this.turnLeft);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					this.phase = Phase.FINAL_FRWD;
					logger.info("Exiting TURN_L -> FINAL_FRWD");
				}
			}
				
			if (this.phase == Phase.FINAL_FRWD) {
				commander.setCommand(this.forwardToCoast);
				result = commander.nextAction();
				
				if (result.isPresent()) {
					decision = result.get();
				} else {
					logger.info("Exiting FINAL_FRWD -> IslandRecon");
					return Optional.empty();
				}
			}
		}
		return Optional.of(decision);
	}
}

