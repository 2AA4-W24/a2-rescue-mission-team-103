package ca.mcmaster.se2aa4.island.team103.ScannerTools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.*;

public class Decider {
	private final Logger logger = LogManager.getLogger();
	
	private enum DecisionStatus {
		Echo,
		StopDecision
	}
	private int counter = 0;
	DecisionStatus decisionStatus = DecisionStatus.Echo;

	public JSONObject performDecision(Drone drone, TurnDirection turn, History<JSONObject> respHistory){
		
		if(counter == 2){
			decisionStatus = DecisionStatus.Echo;
		}
		counter++;
		JSONObject decision = new JSONObject();
		logger.info("AAHHH {}", decisionStatus);
		switch(decisionStatus){
			case Echo:
				logger.info("HEEERE");
				decision.put("response",drone.echoForward());
				decision.put("done","proceed");
				decisionStatus = DecisionStatus.StopDecision;
				break;
			case StopDecision:
				logger.info("PAY ATTENTION:{}",respHistory.getLast());
				if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
					if(turn.equals(TurnDirection.Right)){
						decision.put("response",drone.turnLeft());
					}else{
						decision.put("response",drone.turnRight());
					}
					decision.put("done","specialTurn");
				}else{
					decision = drone.scan();
					decision.put("done","proceed");
				}
				decisionStatus = DecisionStatus.Echo;
				break;
		}
		return decision;
	}
}
