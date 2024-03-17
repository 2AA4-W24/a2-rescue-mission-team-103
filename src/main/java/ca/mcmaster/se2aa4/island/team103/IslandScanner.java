package ca.mcmaster.se2aa4.island.team103;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.ScannerTools.*;

public class IslandScanner implements DroneController {

	private final static Logger logger = LogManager.getLogger();

	private enum ScannerPhase{
		ECHO,
		DECISION, // determining whether nav is over using an echo.
		SLICE, // moving across map and performing scans
		TURN, // regular u-turn, to be completed at end of each slice.
		TURNAROUND, // special turn at end of map to return.
		TURNAROUND2 // second special turn (depending on the way interlaced scan works out (last
		            // vs. second to last strip of land))
	}

	private ScannerPhase phase = ScannerPhase.ECHO;
	private TurnDirection turn = TurnDirection.LEFT;
	private boolean firstCall = true;
	private int moves_since_last_special = 0;
	private boolean counter_activator = false;
	private int scan_pass_num = 1;

	private UTurn turner = new UTurn();
	private Slicer slicer = new Slicer();
	private Decider decider = new Decider();
	private Turnaround turnaround = new Turnaround();

	private final static String RESPONSE = "response";
	private final static String DONE = "done";
	
	public Optional<JSONObject> nextAction(Drone drone, History<JSONObject> respHistory){

		logger.info("Scanning Phase: {}",phase);

		if(counter_activator){
			moves_since_last_special++;
		}

		JSONObject response = new JSONObject();
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

		if(phase.equals(ScannerPhase.ECHO)){
			phase = ScannerPhase.DECISION;
			decision = drone.echoForward();
			return Optional.of(decision);
		}

		if(phase.equals(ScannerPhase.DECISION)){
			response = decider.performDecision(drone, respHistory, scan_pass_num, turn, moves_since_last_special);
			if(response.getString(DONE).equals("specialTurn")){
				counter_activator = true;
				scan_pass_num = 2;
				phase = ScannerPhase.TURNAROUND; 
			}else if(response.getString(DONE).equals("specialTurn2")){
				if(turn.equals(TurnDirection.LEFT)){
					turn = TurnDirection.RIGHT;
				}else{
					turn = TurnDirection.LEFT;
				}
				phase = ScannerPhase.TURNAROUND2;
			}else if(response.getString(DONE).equals("proceed")){
				phase = ScannerPhase.SLICE;
			}
			else if(response.getString(DONE).equals("over")){
				phase = ScannerPhase.ECHO;
				return Optional.empty();
			}
		}
		if(phase.equals(ScannerPhase.SLICE)){
			response = slicer.performSlice(drone, turn, respHistory);
			if(response.has("done")){ 
				phase = ScannerPhase.TURN; 
			}else{
				decision = response.getJSONObject(RESPONSE);
				return Optional.of(decision);
			}
		}
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
