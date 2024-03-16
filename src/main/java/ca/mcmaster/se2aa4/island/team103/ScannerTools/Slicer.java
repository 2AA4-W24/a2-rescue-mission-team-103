package ca.mcmaster.se2aa4.island.team103.ScannerTools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import java.util.Optional;
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

	public JSONObject performSlice(Drone drone, TurnDirection turn, History<JSONObject> respHistory){

		JSONObject decision = new JSONObject();
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
					travelStatus = SliceStatus.TurnWait;
				}else{
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
						if(turn.equals(TurnDirection.Right)){
							decision.put("response",drone.turnRight());
						}else{
							decision.put("response",drone.turnLeft());
						}
						furtherDistance = 0;
						decision.put("done",true);
						travelStatus = SliceStatus.Scan;
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
}
