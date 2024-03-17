package ca.mcmaster.se2aa4.island.team103.ScannerTools;


import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.*;

public class Slicer {

	private int furtherDistance = 0; // Used for tracking moves beyond island edge for proper turaround.

	private enum SliceStatus {
		SCAN,
		FORWARD,
		DECISION,
		TURNWAIT // continuing to travel once island is done for proper turnaround.
	}

	private final static String RESPONSE = "response";
	private final static String EXTRAS = "extras";
	private final static String FOUND = "found";

	SliceStatus travelStatus = SliceStatus.SCAN;

	public JSONObject performSlice(Drone drone, TurnDirection turn, History<JSONObject> respHistory){
		JSONObject decision = new JSONObject();
		switch(travelStatus){
			case SCAN:
				decision.put(RESPONSE,drone.scan());
				travelStatus = SliceStatus.FORWARD;
				break;
			case FORWARD:
				JSONArray biomesObj = respHistory.getLast().getJSONObject(EXTRAS).getJSONArray("biomes");
				if(biomesObj.getString(0).equals("OCEAN") && biomesObj.length() == 1){
					decision.put(RESPONSE,drone.echoForward());
					travelStatus = SliceStatus.DECISION;
					break;
				}
				if(travelStatus != SliceStatus.DECISION){
					decision.put(RESPONSE,drone.flyForwards());
					travelStatus = SliceStatus.SCAN;
				}
				break;
			case DECISION:
				if(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("OUT_OF_RANGE")){
					if(turn.equals(TurnDirection.RIGHT)){
						decision.put(RESPONSE,drone.echoRight());
					}else{
						decision.put(RESPONSE,drone.echoLeft());
					}
					furtherDistance++;
					travelStatus = SliceStatus.TURNWAIT;
				}else{
					decision.put(RESPONSE,drone.flyForwards());
					travelStatus = SliceStatus.SCAN;
				}
				break;
			case TURNWAIT:
				if(furtherDistance % 2 == 0){
					if(turn.equals(TurnDirection.RIGHT)){
						decision.put(RESPONSE,drone.echoRight());
					}else{
						decision.put(RESPONSE,drone.echoLeft());
					}
					furtherDistance++;
				}else{
					if(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("OUT_OF_RANGE") || 
					(respHistory.getLast().getJSONObject(EXTRAS).getString(FOUND).equals("GROUND") && 
					respHistory.getLast().getJSONObject(EXTRAS).getInt("range") > 2)){
						furtherDistance = 0;
						decision.put("done",true);
					}else{
						decision.put(RESPONSE,drone.flyForwards());
						furtherDistance++;
					}
				}
				travelStatus = SliceStatus.SCAN;
				break;
		}
		return decision;
	}
}
