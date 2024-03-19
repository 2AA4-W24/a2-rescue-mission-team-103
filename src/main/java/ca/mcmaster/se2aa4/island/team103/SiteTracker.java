package ca.mcmaster.se2aa4.island.team103;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;

import org.json.JSONObject;

public class SiteTracker {
	private List<PointOfInterest> inlets = new ArrayList<PointOfInterest>();
	private Optional<PointOfInterest> site = Optional.empty();
	private DistanceCalculation calculator = new DistanceCalculation();
	private final Logger logger = LogManager.getLogger();
	private PointOfInterest closest_inlet;

	private void addInlet(String id, Coordinate coord) {
		PointOfInterest new_inlet = new Inlet(id, coord);
		logger.info("Adding Inlet: {}", new_inlet.id());
		this.inlets.add(new_inlet);
	}

	private void addRescueSite(String id, Coordinate coord) {
		this.site = Optional.of(new Site(id, coord));
		logger.info("Adding Rescue Site: {}", site.get().id());
	}

	public String getClosestInlet() {
		
		if (site.isPresent()) {
			closest_inlet = calculator.returnClosestInlet(inlets, site.get());
		} else {
			logger.warn("Site is not present, returning first inlet");
			closest_inlet = inlets.get(0);
		}
		return closest_inlet.id();
	}

	public void findPointsOfInterest(List<JSONObject> history, List<Coordinate> coordHistory){
		for(int i=0; i<history.size(); i++){
			JSONObject current_obj = history.get(i);
			JSONObject extras = current_obj.getJSONObject("extras");
			if(extras.has("biomes")){
				if(extras.getJSONArray("creeks").length() > 0){
					String id = extras.getJSONArray("creeks").getString(0);
					addInlet(id,new Coordinate(coordHistory.get(i).x(),coordHistory.get(i).y()));
					
				}else if(extras.getJSONArray("sites").length() > 0){
					String id = extras.getJSONArray("sites").getString(0);
					addRescueSite(id,new Coordinate(coordHistory.get(i).x(),coordHistory.get(i).y()));
				}
			}
		}
	}
}
