package ca.mcmaster.se2aa4.island.team103;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.ScannerTools.*;

public class IslandScanner implements DroneController {

	private final Logger logger = LogManager.getLogger();

	private enum ScannerPhase{
		Echo,
		Decision, // determining whether nav is over using an echo.
		Slice, // moving across map and performing scans
		Turn, // regular u-turn, to be completed at end of each slice.
		Turnaround, // special turn at end of map to return.
		Turnaround2 // second special turn (depending on the way interlaced scan works out (last
		            // vs. second to last strip of land))
	}

	private ScannerPhase phase = ScannerPhase.Echo;
	private TurnDirection turn = TurnDirection.Left;
	private boolean firstCall = true;
	private int moves_since_last_special = 0;
	private boolean counter_activator = false;
	private int scan_pass_num = 1;
	private Drone drone;
	private History<JSONObject> respHistory;

	private UTurn turner = new UTurn();
	private Slicer slicer = new Slicer();
	private Decider decider = new Decider();
	private Turnaround turnaround = new Turnaround();

	public IslandScanner(Drone drone_in, History<JSONObject> history_in) {
		this.drone = drone_in;
		this.respHistory = history_in;
	}
	
	public Optional<JSONObject> nextAction(){

		logger.info("Scanning Phase: {}",phase);

		if(counter_activator){
			moves_since_last_special++;
		}

		JSONObject response = new JSONObject();
		JSONObject decision = new JSONObject();

		// Special block to be used on first call to set what direction the first u-turn should be.
		if(firstCall){
			if(drone.getHeading().equals(Direction.EAST) || drone.getHeading().equals(Direction.SOUTH)){
				turn = TurnDirection.Left;
			}else{
				turn = TurnDirection.Right;
			}
			firstCall = false;
		}

		if(phase.equals(ScannerPhase.Echo)){
			phase = ScannerPhase.Decision;
			decision = drone.echoForward();
			return Optional.of(decision);
		}

		if(phase.equals(ScannerPhase.Decision)){
			response = decider.performDecision(drone, respHistory, scan_pass_num, turn, moves_since_last_special);
			if(response.getString("done").equals("specialTurn")){
				counter_activator = true;
				scan_pass_num = 2;
				phase = ScannerPhase.Turnaround;
				if(turn.equals(TurnDirection.Left)){
					turn = TurnDirection.Right;
				}else{
					turn = TurnDirection.Left;
				} 
			}else if(response.getString("done").equals("specialTurn2")){
				phase = ScannerPhase.Turnaround2;
			}else if(response.getString("done").equals("proceed")){
				phase = ScannerPhase.Slice;
			}
			else if(response.getString("done").equals("over")){
				phase = ScannerPhase.Echo;
				return Optional.empty();
			}
		}
		if(phase.equals(ScannerPhase.Slice)){
			response = slicer.performSlice(drone, turn, respHistory);
			if(response.has("done")){ 
				phase = ScannerPhase.Turn; 
			}else{
				decision = response.getJSONObject("response");
				return Optional.of(decision);
			}
		}
		if(phase.equals(ScannerPhase.Turn)){
			response = turner.performUTurn(drone, turn);
			decision = response.getJSONObject("response");
			if(response.getBoolean("done")){
				if(turn.equals(TurnDirection.Right)){
					turn = TurnDirection.Left;
				}else{
					turn = TurnDirection.Right;
				}
				phase = ScannerPhase.Echo;
			}
		}
		if(phase.equals(ScannerPhase.Turnaround)){
			response = turnaround.specialTurn(drone,respHistory,turn);
			if(response.has("done")){
				phase = ScannerPhase.Decision;
				decision = drone.echoForward(); // CHANGE HERE FOR TESTING PURPOSES
				return Optional.of(decision);
			}else{
				decision = response.getJSONObject("response");
				return Optional.of(decision);
			}
		}
		if(phase.equals(ScannerPhase.Turnaround2)){
			response = turnaround.specialTurn2(drone,respHistory,turn);
			if(response.has("done")){
				phase = ScannerPhase.Decision;
				decision = drone.echoForward();
				return Optional.of(decision);
			}else{
				decision = response.getJSONObject("response");
				return Optional.of(decision);
			}
		}
		
		return Optional.of(decision);
	}
}
