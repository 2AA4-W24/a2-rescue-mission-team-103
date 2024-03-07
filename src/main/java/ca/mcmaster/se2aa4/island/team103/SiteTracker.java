package ca.mcmaster.se2aa4.island.team103;
import java.util.ArrayList;
import java.util.List;

public class SiteTracker {
	private List<PointOfIntrest> inlets = new ArrayList<PointOfIntrest>();
	private PointOfIntrest site;

	public void addInlet(String id, Coordinate coord) {
		PointOfIntrest new_inlet = new Inlet(id, coord);
		inlets.add(new_inlet);
	}

	public void addRescueSite(String id, Coordinate coord) {
		site = new Site(id, coord);
	}

	public String getClosestInlet() {
		return "none";
	}
}
