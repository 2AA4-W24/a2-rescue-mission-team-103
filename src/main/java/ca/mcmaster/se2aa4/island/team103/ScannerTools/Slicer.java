package ca.mcmaster.se2aa4.island.team103.ScannerTools;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.*;

public class Slicer {

	private int furtherDistance = 0; // Used for tracking moves beyond island edge for proper turaround.

	private enum SliceStatus {
		Scan,
		Forward,
		Decision,
		TurnWait // continuing to travel once island is done for proper turnaround.
	}

	SliceStatus travelStatus = SliceStatus.Scan;

	private int distanceToLand = 0;
	
	private final Logger logger = LogManager.getLogger();

	public JSONObject performSlice(Drone drone, TurnDirection turn, History<JSONObject> respHistory, boolean flyNoScan){
		if(flyNoScan){
			travelStatus = SliceStatus.Decision;
		}
		logger.info("LL Slicer Phase: {}",travelStatus);
		logger.info("distanceToLand: {}",distanceToLand);
		JSONObject decision = new JSONObject();
		if(distanceToLand >= 1){
			logger.info("INDIST");
			decision.put("response",drone.flyForwards());
			travelStatus = SliceStatus.Scan;
			distanceToLand--;
			return decision;
		}else{
			distanceToLand = 0;
		}
		switch(travelStatus){
			case Scan:
				decision.put("response",drone.scan());
				travelStatus = SliceStatus.Forward;
				break;
			case Forward:
				JSONArray biomesObj = respHistory.getLast().getJSONObject("extras").getJSONArray("biomes");
				if(biomesObj.getString(0).equals("OCEAN") && biomesObj.length() == 1){
					decision.put("response",drone.echoForward());
					travelStatus = SliceStatus.Decision;
					break;
				}
				if(travelStatus != SliceStatus.Decision){
					decision.put("response",drone.flyForwards());
					travelStatus = SliceStatus.Scan;
				}
				break;
			case Decision:
				if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
					if(turn.equals(TurnDirection.Right)){
						decision.put("response",drone.echoRight());
					}else{
						decision.put("response",drone.echoLeft());
					}
					furtherDistance++;
					travelStatus = SliceStatus.TurnWait;
				}else if(respHistory.getLast().getJSONObject("extras").getInt("range") > 1){
					logger.info("IN branch now");
					distanceToLand = respHistory.getLast().getJSONObject("extras").getInt("range");
					decision.put("response",drone.flyForwards());
					distanceToLand--;
				}
				else{
					decision.put("response",drone.flyForwards());
					travelStatus = SliceStatus.Scan;
				}
				break;
			case TurnWait:
				if(furtherDistance % 2 == 0){
					if(turn.equals(TurnDirection.Right)){
						decision.put("response",drone.echoRight());
					}else{
						decision.put("response",drone.echoLeft());
					}
					furtherDistance++;
				}else{
					if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE") || 
					(respHistory.getLast().getJSONObject("extras").getString("found").equals("GROUND") && 
					respHistory.getLast().getJSONObject("extras").getInt("range") > 2)){
						furtherDistance = 0;
						decision.put("done",true);
					}else{
						decision.put("response",drone.flyForwards());
						furtherDistance++;
					}
				}
				travelStatus = SliceStatus.Scan;
				break;
		}
		return decision;
	}

	public JSONObject flyForwardsNoScan(Drone drone, int n){
		JSONObject decision = new JSONObject();
		return decision;
	}
}
