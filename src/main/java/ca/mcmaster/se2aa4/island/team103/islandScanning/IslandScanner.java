package ca.mcmaster.se2aa4.island.team103.islandScanning;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.drone.Direction;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.drone.DroneController;
import ca.mcmaster.se2aa4.island.team103.history.History;

public class IslandScanner implements DroneController {
	// Overall class for handling the scanning of the island.
	
	private static final Logger logger = LogManager.getLogger();

	private enum ScannerPhase{
		ECHO,
		DECISION, // determining whether nav is over using an echo.
		SLICE, // moving across map and performing scans
		TURN, // regular u-turn, to be completed at end of each slice.
		TURNAROUND, // special turn at end of map to return.
		TURNAROUND2 // second special turn (depending on the way interlaced scan works out (last
		            // vs. second to last strip of land))
	}

	// Phase & direction tracking
	private ScannerPhase phase = ScannerPhase.ECHO;
	private TurnDirection turn = TurnDirection.LEFT;
	
	// Special tracker variables for handling special turns at end of island, as well as first method setup needed.
	private boolean firstCall = true;
	private int moves_since_last_special = 0;
	private boolean counter_activator = false;
	private int scan_pass_num = 1;
	
	private Drone drone;
	private History<JSONObject> respHistory;

	// Objects to handle each component of the scanning.
	private UTurn turner = new UTurn();
	private Slicer slicer = new Slicer();
	private Decider decider = new Decider();
	private Turnaround turnaround = new Turnaround();

	private static final String RESPONSE = "response";
	private static final String DONE = "done";

	private boolean flyNoScan = false;
  
	public IslandScanner(Drone drone_in, History<JSONObject> history_in) {
		this.drone = drone_in;
		this.respHistory = history_in;
	}
	
	public Optional<JSONObject> nextAction(){

		logger.info("Scanning Phase: {}",phase);

		if(counter_activator){
			moves_since_last_special++;
		}

		JSONObject response;
		JSONObject decision = new JSONObject();

		// Special block to be used on first call to set what direction the first u-turn should be.
		if(firstCall){
			if(drone.getHeading().equals(Direction.EAST) || drone.getHeading().equals(Direction.SOUTH)){
				turn = TurnDirection.LEFT;
			}else{
				turn = TurnDirection.RIGHT;
			}
			firstCall = false;
		}

		// Starting with echo, and heading to decision.
		if(phase.equals(ScannerPhase.ECHO)){
			phase = ScannerPhase.DECISION;
			decision = drone.echoForward();
			return Optional.of(decision);
		}

		// Deciding what to do next (special turn, end, or continue.)
		if(phase.equals(ScannerPhase.DECISION)){
			response = decider.performDecision(drone, respHistory, scan_pass_num, moves_since_last_special);
			if(response.getString(DONE).equals("specialTurn")){
				counter_activator = true;
				scan_pass_num = 2;
				phase = ScannerPhase.TURNAROUND;
				if(turn.equals(TurnDirection.LEFT)){
					turn = TurnDirection.RIGHT;
				}else{
					turn = TurnDirection.LEFT;
				} 
			}else if(response.getString("done").equals("specialTurn2")){
				phase = ScannerPhase.TURNAROUND2;
			}else if(response.getString("done").equals("proceed")){
				phase = ScannerPhase.SLICE;
				flyNoScan = true;
			}
			else if(response.getString("done").equals("proceedToLand")){
				phase = ScannerPhase.SLICE;
				flyNoScan = true;
			}
			else if(response.getString("done").equals("over")){
				phase = ScannerPhase.ECHO;
				return Optional.empty();
			}
		}

		// Slice is the usual navigation of travelling forwards across a strip of land and scanning.
		if(phase.equals(ScannerPhase.SLICE)){
			response = slicer.performSlice(drone, turn, respHistory, flyNoScan);
			flyNoScan = false;
			if(response.has("done")){ 
				phase = ScannerPhase.TURN; 
			}else{
				decision = response.getJSONObject(RESPONSE);
				return Optional.of(decision);
			}
		}

		// U-Turn done at the end of each slice of land.
		if(phase.equals(ScannerPhase.TURN)){
			response = turner.performUTurn(drone, turn);
			decision = response.getJSONObject(RESPONSE);
			if(response.getBoolean(DONE)){
				if(turn.equals(TurnDirection.RIGHT)){
					turn = TurnDirection.LEFT;
				}else{
					turn = TurnDirection.RIGHT;
				}
				phase = ScannerPhase.ECHO;
			}
		}

		// Special turn used at the end of the island to go back to the starting point (sometimes require two depending on alignment)
		// as well as where first scan ends.
		if(phase.equals(ScannerPhase.TURNAROUND)){
			response = turnaround.specialTurn(drone,respHistory,turn);
			if(response.has(DONE)){
				phase = ScannerPhase.DECISION;
				decision = drone.echoForward();
				return Optional.of(decision);
			}else{
				decision = response.getJSONObject(RESPONSE);
				return Optional.of(decision);
			}
		}
		if(phase.equals(ScannerPhase.TURNAROUND2)){
			response = turnaround.specialTurn2(drone,respHistory,turn);
			if(response.has(DONE)){
				phase = ScannerPhase.DECISION;
				decision = drone.echoForward();
				return Optional.of(decision);
			}else{
				decision = response.getJSONObject(RESPONSE);
				return Optional.of(decision);
			}
    }
		return Optional.of(decision);
	}
}
