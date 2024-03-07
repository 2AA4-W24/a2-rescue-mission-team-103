package ca.mcmaster.se2aa4.island.team103;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

public class ImportantScans {
	private List<JSONObject> scans = new ArrayList<JSONObject>();

	public ImportantScans(ResponseHistory history, NavHistory coordHistory){
		for(int i=0; i<history.getSize(); i++){
			JSONObject current_obj = history.getItem(i);
			if(current_obj.has("biomes")){
				if(current_obj.getJSONArray("creeks").length() > 0 || current_obj.getJSONArray("sites").length()>0){
					JSONObject new_input = new JSONObject();
					new_input.put("scan",current_obj);
					new_input.put("coordinates",coordHistory.getItem(i));
				}
			}
		}
	}
}
