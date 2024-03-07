package ca.mcmaster.se2aa4.island.team103;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

public class ScanHistory {
	private List<JSONObject> scans = new ArrayList<JSONObject>();

	public ScanHistory(ResponseHistory history){
		for(int i=0; i<history.getSize(); i++){
			JSONObject current_obj = history.getItem(i);
			if(current_obj.has("biomes")){
				scans.add(current_obj);
			}
		}
	}
}
