package ca.mcmaster.se2aa4.island.team103.islandLocating;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.drone.DroneController;
import ca.mcmaster.se2aa4.island.team103.history.Action;
import ca.mcmaster.se2aa4.island.team103.history.History;

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
	private Command uturn;
	private Command forwardToCoast;
	private Command travelToEnd;
	private Command echoSearch;
	
	private static final String GROUND = "GROUND";
	private static final String EXTRAS = "extras";
	private static final String RANGE = "range";
	private static final String FOUND = "found";
  
	// Class to handle finding the island, which uses different Commands to delegate the workload
	public IslandLocator(Drone drone_in, History<JSONObject> history_in) {
		this.drone = drone_in;
		this.history = history_in;
		this.turnRight = new TurnRight(this.drone);
		this.turnLeft = new TurnLeft(this.drone);
		this.uturn = new UturnLeft(this.drone);
		this.forwardToCoast = new FinalForward(this.drone, this.history);
		this.travelToEnd = new TravelToEnd(this.drone, this.history);
		this.echoSearch = new EchoSearch(this.drone);
	}
	
	public Optional<JSONObject> nextAction() {
		/* Returns the next action the drone should take 
		 * Broken into 5 distinct phases, with two sub-phases.
		 * 
		 * - Search Phase (Phase.SEARCH)
		 * 		- Echo Search Sub-phase (Phase.SEARCH + Action.ECHO) -> Command: EchoSearch
		 * 		- Movement Sub-phase (Phase.SEARCH + Action.MOVE) -> Responsibility of IslandLocator as it requires knowledge that Commands aren't given
		 * - Travel to End Phase (Phase.TRAVEL_TO_END) -> Command: TravelToEnd
		 * - Left Uturn Phase (Phase.UTURN_L) -> Command: UturnLeft
		 * - Right Turn Phase (Phase.TURN_R) -> Command: TurnRight
		 * - Left Turn Phase (Phase.TURN_L) -> Command: TurnLeft
		 * - Forwards to Coast Phase (Phase.FINAL_FRWD) -> Command: FinalFoward
		*/
		JSONObject decision = new JSONObject();
		Optional<JSONObject> result;
		List<JSONObject> echo_results;
    
		if (this.phase == Phase.SEARCH) {
			/*  The initial phase of the drone. Searches left, right and forwards for island while moving forwards */

			if (next_action == Action.ECHO) {	
				// Echo sub-phase - sends an echo in all three directions.
				
				commander.setCommand(echoSearch);
				result = commander.nextAction();

				if (result.isPresent()) {
					decision = result.get();
				} else {
					next_action = Action.MOVE;
				}
			}
				
			if (next_action == Action.MOVE) {
				// Movement sub-phase. Makes a decision on whether to exit phase or continue based on Echo results.

				next_action = Action.ECHO;
				echo_results = history.getItems(-3);
				List<String> echo_found = new ArrayList<>(3);
				List<Integer> echo_range = new ArrayList<>(3);

				for (int i = 0; i < echo_results.size(); i++) {
					echo_found.add(echo_results.get(i).getJSONObject(EXTRAS).getString(FOUND));
					echo_range.add(echo_results.get(i).getJSONObject(EXTRAS).getInt(RANGE));
				}

				if (echo_found.get(0).equals(GROUND)) {
					this.phase = Phase.TURN_R;
					logger.info("Exiting Search Phase -> TURN_R");
				}
				else if (echo_found.get(1).equals(GROUND) && echo_range.get(1) == 2) {
					this.phase = Phase.TRAVEL_TO_END;
					logger.info("Exiting Search Phase -> TRAVEL_TO_END");
				}
				else if (echo_found.get(2).equals(GROUND)) {
					this.phase = Phase.TURN_L;
					logger.info("Exiting Search Phase -> TURN_L");
				}
				else {
					decision = drone.flyForwards();
				}
			}
		}

		if (this.phase == Phase.TRAVEL_TO_END) {
			// Travel to End phase. Travels to end of map in case of meeting island head-on.
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
			// Left Uturn Phase. Performs simple Uturn via Left->Left.
			commander.setCommand(this.uturn);
			result = commander.nextAction();

			if (result.isPresent()) {
				decision = result.get();
			} else {
				this.phase = Phase.FINAL_FRWD;
				logger.info("Exiting UTURN_L -> FINAL_FRWD");
			}
		}

		if (this.phase == Phase.TURN_R) {
			// Turn Right Phase. Performs special on-spot right turn.
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
			// Turn Left Phase. Performs special on-spot left turn.
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
			// Forward to Coast phase. Flies forwards until it encounters coast. Terminal phase, returns Optional.empty() once complete.
			commander.setCommand(this.forwardToCoast);
			result = commander.nextAction();
			
			if (result.isPresent()) {
				decision = result.get();
			} else {
				logger.info("Exiting FINAL_FRWD -> IslandRecon");
				return Optional.empty();
			}
		}
		return Optional.of(decision);
	}
}

