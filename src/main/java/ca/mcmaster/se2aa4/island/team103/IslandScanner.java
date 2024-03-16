package ca.mcmaster.se2aa4.island.team103;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.ScannerTools.UTurn;
import ca.mcmaster.se2aa4.island.team103.ScannerTools.Slicer;
import ca.mcmaster.se2aa4.island.team103.ScannerTools.Decider;

public class IslandScanner implements DroneController {

	private final Logger logger = LogManager.getLogger();

	private enum ScannerPhase{
		Decision, // determining whether nav is over using an echo.
		Slice, // moving across map and performing scans
		Turn, // regular u-turn, to be completed at end of each slice.
		SpecialTurn // special turn at end of map to return.
	}

	private ScannerPhase phase = ScannerPhase.Decision;
	private TurnDirection turn;
	private boolean firstCall = true;
	private int specialTurnCounter = 0;

	private UTurn turner = new UTurn();
	private Slicer slicer = new Slicer();
	private Decider decider = new Decider();
	
	public Optional<JSONObject> nextAction(Drone drone, History<JSONObject> respHistory){

		JSONObject response = new JSONObject();
		JSONObject decision = new JSONObject();
		logger.info("Turn change #{}",specialTurnCounter);
		// Special block to be used on first call to set what direction the first u-turn should be.
		if(firstCall){
			if(drone.getHeading().equals(Direction.EAST) || drone.getHeading().equals(Direction.SOUTH)){
				turn = TurnDirection.Left;
			}else{
				turn = TurnDirection.Right;
			}
			firstCall = false;
		}
		switch(phase){
			case Decision:
				specialTurnCounter++;
				logger.info("Decision #{}", specialTurnCounter);
				response = decider.performDecision(drone, turn, respHistory);
				decision = response.getJSONObject("response");
				if(response.getString("done").equals("specialTurn")){
					phase = ScannerPhase.SpecialTurn; 
				}else if(response.getString("done").equals("proceed")){
					phase = ScannerPhase.Slice;
				}
				break;
			case Slice:
				response = slicer.performSlice(drone, turn, respHistory);
				decision = response.getJSONObject("response");
				if(response.has("done")){ 
					phase = ScannerPhase.Turn; 
				}
				break;
			case Turn:
				response = turner.performUTurn(drone, turn);
				decision = response.getJSONObject("response");
				if(turn.equals(TurnDirection.Right)){
					turn = TurnDirection.Left;
				}else{
					turn = TurnDirection.Right;
				}
				phase = ScannerPhase.Decision;
				break;
			case SpecialTurn:
				return Optional.empty();
		}
		logger.info("Decision is {}", decision);
		return Optional.of(decision);
	}
}
