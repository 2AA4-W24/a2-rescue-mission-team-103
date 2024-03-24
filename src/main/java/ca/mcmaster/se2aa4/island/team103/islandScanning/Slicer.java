package ca.mcmaster.se2aa4.island.team103.islandScanning;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.history.History;

public class Slicer {

	private int furtherDistance = 0; // Used for tracking moves beyond island edge for proper turaround.

	private enum SliceStatus {
		SCAN,
		FORWARD,
		DECISION,
		TURNWAIT // continuing to travel once island is done for proper turnaround.
	}

	private static final String RESPONSE = "response";
	private static final String EXTRAS = "extras";
	private static final String FOUND = "found";
	private static final String RANGE = "range";

	SliceStatus travelStatus = SliceStatus.SCAN;

	private int distanceToLand = 0;

	public JSONObject performSlice(Drone drone, TurnDirection turn, History<JSONObject> respHistory, boolean flyNoScan){
		
		JSONObject decision = new JSONObject();
		
		// In the case land is not immediately present, can proceed until the island without scanning.
		if(flyNoScan){
			travelStatus = SliceStatus.DECISION;
		}
		
		if(distanceToLand >= 1){
			decision.put(RESPONSE,drone.flyForwards());
			travelStatus = SliceStatus.SCAN;
			distanceToLand--;
			return decision;
		}else{
			distanceToLand = 0;
		}

		// Handling phases of a slice (scanning, moving, and deciding when to turn around.)
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
				}else if(respHistory.getLast().getJSONObject(EXTRAS).getInt(RANGE) > 1){
					distanceToLand = respHistory.getLast().getJSONObject(EXTRAS).getInt(RANGE);
					decision.put(RESPONSE,drone.flyForwards());
					distanceToLand--;
				}
				else{
					decision.put(RESPONSE,drone.flyForwards());
					travelStatus = SliceStatus.SCAN;
				}
				break;
			case TURNWAIT:
				// To ensure that land is not being missed, continue travelling forward while land exists to the direction the drone is about to turn.
				// As soon as we will not be missing land in the next slice, we can turn around.
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
					respHistory.getLast().getJSONObject(EXTRAS).getInt(RANGE) > 2)){
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
