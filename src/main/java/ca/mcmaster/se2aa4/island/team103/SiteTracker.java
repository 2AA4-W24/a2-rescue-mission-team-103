package ca.mcmaster.se2aa4.island.team103;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class SiteTracker {
	private List<PointOfInterest> inlets = new ArrayList<PointOfInterest>();
	private PointOfInterest site;
	private DistanceCalculation calculator = new DistanceCalculation();

	private void addInlet(String id, Coordinate coord) {
		PointOfInterest new_inlet = new Inlet(id, coord);
		inlets.add(new_inlet);
	}

	private void addRescueSite(String id, Coordinate coord) {
		site = new Site(id, coord);
	}

	public String getClosestInlet() {
		PointOfInterest closest_inlet = calculator.returnClosestInlet(inlets, site);
		return closest_inlet.id();
	}

	public void findPointsOfInterest(List<JSONObject> history, List<Coordinate> coordHistory){
		for(int i=0; i<history.size(); i++){
			JSONObject current_obj = history.get(i);
			if(current_obj.has("biomes")){
				if(current_obj.getJSONArray("creeks").length() > 0){
					String id = current_obj.getJSONArray("creeks").getString(0);
					addInlet(id,new Coordinate(coordHistory.get(i).x(),coordHistory.get(i).y()));
				}else if(current_obj.getJSONArray("sites").length() > 0){
					String id = current_obj.getJSONArray("sites").getString(0);
					addRescueSite(id,new Coordinate(coordHistory.get(i).x(),coordHistory.get(i).y()));
				}
			}
		}
	}
}
