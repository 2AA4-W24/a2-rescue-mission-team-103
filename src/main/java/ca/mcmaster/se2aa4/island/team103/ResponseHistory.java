package ca.mcmaster.se2aa4.island.team103;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseHistory {
	private List<JSONObject> responseHistory = new ArrayList<JSONObject>();

	public void addItem(JSONObject j){
		responseHistory.add(j);
	}

	public JSONObject getLast(){
		return responseHistory.get(responseHistory.size()-1);
	}
}
