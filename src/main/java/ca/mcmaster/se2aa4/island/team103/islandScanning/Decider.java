package ca.mcmaster.se2aa4.island.team103.islandScanning;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.history.History;

public class Decider {

	// Handling specific case of after the U-Turn, deciding whether to continue with island scanning, turn around, or stop the drone.
	public JSONObject performDecision(Drone drone, History<JSONObject> respHistory, int scan_pass, int moves_since_last_special){
		JSONObject decision = new JSONObject();

		if(respHistory.getLast().getJSONObject("extras").getString("found").equals("OUT_OF_RANGE")){
			if(scan_pass == 1){
				decision.put("done","specialTurn");
			}
			else if(scan_pass == 2 && moves_since_last_special < 20){
				decision.put("done","specialTurn2");
			}
			else if(scan_pass == 2){
				decision.put("done","over");
			}
		}else if(respHistory.getLast().getJSONObject("extras").getInt("range") > 1){
			decision = drone.flyForwards();
			decision.put("done","proceedToLand");
		}else{
			decision = drone.scan();
			decision.put("done","proceed");
		}
		return decision;
	}
}
